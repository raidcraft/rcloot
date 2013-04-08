package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.database.tables.LootTablesTable;
import de.raidcraft.loot.exceptions.LootTableNotExistsException;
import de.raidcraft.loot.object.*;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.table.SimpleLootTable;
import de.raidcraft.loot.table.SimpleLootTableEntry;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.TreasureRewardLevel;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 18.10.12 - 20:57
 * Description:
 */
public class LootFactory {

    public final static String ANY = "ANY";
    public final static LootFactory INST = new LootFactory();
    // sort loot objects by x and z coordinates
    public Map<Chunk, Map<Block, LootObject>> loadedObjects = new HashMap<>();

    public void deleteLootObject(LootObject lootObject, boolean andTable) {

        RaidCraft.getTable(LootObjectsTable.class).deleteObject(lootObject);

        if (andTable && !(lootObject instanceof TreasureLootObject)) {
            RaidCraft.getTable(LootTablesTable.class).deleteTable(lootObject.getLootTable());
        }

        unregisterLootObject(lootObject);
    }

    public LootObject getLootObject(Location location) {

        if(!loadedObjects.containsKey(location.getChunk())) {
            return null;
        }

        if(!loadedObjects.get(location.getChunk()).containsKey(location.getBlock())) {
            return null;
        }

        return loadedObjects.get(location.getChunk()).get(location.getBlock());
    }

    private LootTable createLootTable(ItemStack[] items, int minLoot, int maxLoot) {

        List<LootTableEntry> tableEntries = new ArrayList<>();
        // at first, count total items
        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemCount++;
            }
        }
        // then create and add loot entries
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            LootTableEntry tableEntry = new SimpleLootTableEntry();
            tableEntry.setItem(item);
            tableEntry.setChance((int) ((1. / (double) itemCount) * 100.));
            tableEntries.add(tableEntry);
        }
        LootTable lootTable = new SimpleLootTable();
        lootTable.setEntries(tableEntries);
        lootTable.setMinMaxLootItems(minLoot, maxLoot);
        return lootTable;
    }

    public void createTreasureLootObject(String creator, Block block, int rewardLevel) {

        createTreasureLootObject(creator, block, rewardLevel, false);
    }

    public void createTreasureLootObject(String creator, Block block, int rewardLevel, boolean chat) {

        Player player = Bukkit.getPlayer(creator);
        LootObject existingLootObject = LootFactory.INST.getLootObject(block.getLocation());
        if (existingLootObject != null) {
            if (player != null && chat) {
                LootChat.alreadyLootObject(player);
            }
            return;
        }


        SimpleTreasureLootObject treasureLootObject = new SimpleTreasureLootObject();

        try {
            LootTable lootTable = RaidCraft.getTable(LootTablesTable.class).getLootTable(TreasureRewardLevel.getLinkedTable(rewardLevel));
            if (lootTable == null) {
                throw new LootTableNotExistsException("[Loot] Cannot load loot table");
            }
            treasureLootObject.assignLootTable(lootTable);
        } catch (Throwable e) {
            RaidCraft.LOGGER.warning("[Loot] Try to assign non existing loot table (treasure object creation)!");
            if (player != null && chat) {
                LootChat.failureDuringCreation(player);
            }
            return;
        }

        treasureLootObject.setHostLocation(block.getLocation());
        treasureLootObject.setCreator(creator);
        treasureLootObject.setCreated(System.currentTimeMillis() / 1000);
        treasureLootObject.setRewardLevel(rewardLevel);
        treasureLootObject.setEnabled(true);

        // save loot object in database
        RaidCraft.getTable(LootObjectsTable.class).addObject(treasureLootObject);

        // register loot object in cache
        addLootObject(treasureLootObject);

        if (player != null && chat) {
            LootChat.successfullyCreatedLootObject(player, treasureLootObject);
        }
    }

    public void createTimedLootObject(String creator, Block block, ItemStack[] items, int cooldown, int drops) {

        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemCount++;
            }
        }
        if (drops != SettingStorage.ALL) {
            itemCount = drops;
        }
        LootTable lootTable = createLootTable(items, itemCount, itemCount);
        // create loot object
        SimpleTimedLootObject timedLootObject = new SimpleTimedLootObject();
        timedLootObject.setCooldown(cooldown);
        timedLootObject.setHostLocation(block.getLocation());
        timedLootObject.assignLootTable(lootTable);
        timedLootObject.setCreator(creator);
        timedLootObject.setCreated(System.currentTimeMillis() / 1000);
        timedLootObject.setEnabled(true);

        // save loot object in database
        RaidCraft.getTable(LootObjectsTable.class).addObject(timedLootObject);

        // register loot object in cache
        addLootObject(timedLootObject);
    }

    public void createDefaultLootObject(String creator, Block block, ItemStack[] items, int drops) {

        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemCount++;
            }
        }
        if (drops != SettingStorage.ALL) {
            itemCount = drops;
        }
        LootTable lootTable = createLootTable(items, itemCount, itemCount);
        // create loot object
        SimpleLootObject lootObject = new SimpleLootObject();
        lootObject.setHostLocation(block.getLocation());
        lootObject.assignLootTable(lootTable);
        lootObject.setCreator(creator);
        lootObject.setCreated(System.currentTimeMillis() / 1000);
        lootObject.setEnabled(true);

        // save loot object in database
        RaidCraft.getTable(LootObjectsTable.class).addObject(lootObject);

        // register loot object in cache
        addLootObject(lootObject);
    }

    public void createPublicLootObject(String creator, Block block, ItemStack[] items, int cooldown) {
        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemCount++;
            }
        }
        LootTable lootTable = createLootTable(items, itemCount, itemCount);
        // create loot object
        SimplePublicLootObject publicLootObject = new SimplePublicLootObject();
        publicLootObject.setCooldown(cooldown);
        publicLootObject.setHostLocation(block.getLocation());
        publicLootObject.assignLootTable(lootTable);
        publicLootObject.setCreator(creator);
        publicLootObject.setCreated(System.currentTimeMillis() / 1000);
        publicLootObject.setEnabled(true);

        // save loot object in database
        RaidCraft.getTable(LootObjectsTable.class).addObject(publicLootObject);

        // register loot object in cache
        addLootObject(publicLootObject);
    }

    public void addLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHostLocation().getBlock());
        if (otherChestBlock != null) {
            addLootObjectToMap(otherChestBlock.getLocation(), lootObject);
        }

        addLootObjectToMap(lootObject.getHostLocation(), lootObject);
    }

    public void unregisterLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHostLocation().getBlock());
        if (otherChestBlock != null) {
            if(loadedObjects.containsKey(otherChestBlock.getChunk())) {
                loadedObjects.get(otherChestBlock.getChunk()).remove(otherChestBlock);
            }
        }
        if(loadedObjects.containsKey(lootObject.getHostLocation().getChunk())) {
            loadedObjects.get(lootObject.getHostLocation().getChunk()).remove(lootObject.getHostLocation().getBlock());
        }
    }

    private void addLootObjectToMap(Location location, LootObject lootObject) {
        if(!loadedObjects.containsKey(location.getChunk())) {
            Map<Block, LootObject> lootObjects = new HashMap<>();
            lootObjects.put(location.getBlock(), lootObject);
            loadedObjects.put(location.getChunk(), lootObjects);
        }
        else {
            loadedObjects.get(location.getChunk()).put(location.getBlock(), lootObject);
        }
    }

    public String getObjectInfo(Player player, LootObject lootObject) {

        String info = "Typ: ";
        if(lootObject instanceof PublicLootObject) {
            info += "Public-Loot-Objekt, Cooldown: "
                    + ((TimedLootObject) lootObject).getCooldown()
                    + "s";
        }
        else if (lootObject instanceof TimedLootObject) {
            info += "Timed-Loot-Objekt, Cooldown: "
                    + ((TimedLootObject) lootObject).getCooldown()
                    + "s";
        } else if (lootObject instanceof TreasureLootObject) {
            info += "Schatztruhe, Stufe: " + ((TreasureLootObject) lootObject).getRewardLevel();
        } else if (lootObject instanceof SimpleLootObject) {
            info += "Default-Loot-Objekt";
        }

        info += ", Drops: " + lootObject.getLootTable().getMinLootItems() + "-" + lootObject.getLootTable().getMaxLootItems() + ", Ersteller: " + lootObject.getCreator()
                + ", Erstelldatum: " + DateUtil.getDateString(lootObject.getCreated() * 1000);
        return info;
    }

    public void loadObjects(Chunk chunk) {

        if(loadedObjects.containsKey(chunk)) {
            return;
        }

        for(LootObject lootObject : Database.getTable(LootObjectsTable.class).getAllObjectsByChunk(chunk)) {
            addLootObject(lootObject);
        }
    }

    public void unloadObjects(Chunk chunk) {
        loadedObjects.remove(chunk);
    }
}
