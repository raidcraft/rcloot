package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.object.LootObjectStorage;
import de.raidcraft.loot.api.object.PublicLootObject;
import de.raidcraft.loot.api.object.SimpleLootObject;
import de.raidcraft.loot.api.object.SimplePublicLootObject;
import de.raidcraft.loot.api.object.SimpleTimedLootObject;
import de.raidcraft.loot.api.object.SimpleTreasureLootObject;
import de.raidcraft.loot.api.object.TimedLootObject;
import de.raidcraft.loot.api.object.TreasureLootObject;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.exceptions.LootTableNotExistsException;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.loottables.DatabaseLootTableEntry;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableAlias;
import de.raidcraft.loot.tables.TLootTableEntry;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.TreasureRewardLevel;
import de.raidcraft.util.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 18.10.12 - 20:57
 * Description:
 */
public class LootFactory {

    public final static UUID ANY = UUID.fromString("000000f5-2100-41cc-a05d-3ed7da445841");
    public final static UUID AutomaticPlacerSurface = UUID.fromString("000000ef-b1a0-4173-9775-e5c1352a0cf9");
    public final static UUID AutomaticPlacerCave= UUID.fromString("0000008f-00ae-4368-bb33-b6c965a1f3a3");

    private LootObjectStorage lootObjectStorage;
    private LootPlugin plugin;

    public LootFactory(LootPlugin plugin) {

        this.plugin = plugin;
        lootObjectStorage = plugin.getLootObjectStorage();
    }

    public void deleteLootObject(LootObject lootObject, boolean andTable) {

        RaidCraft.getTable(LootObjectsTable.class).deleteObject(lootObject);

        if (andTable && !(lootObject instanceof TreasureLootObject)) {
            lootObject.getLootTable().delete();
        }

        lootObjectStorage.unregisterLootObject(lootObject);
    }

    public LootTable createLootTable(String alias, ItemStack[] items, int minLoot, int maxLoot) {

        List<LootTableEntry> tableEntries = new ArrayList<>();
        // at first, count total items
        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemCount++;
            }
        }
        TLootTable tLootTable = new TLootTable();
        plugin.getDatabase().save(tLootTable);
        LootTable lootTable = new DatabaseLootTable(tLootTable.getId());
        lootTable.setEntries(tableEntries);
        lootTable.setMinMaxLootItems(minLoot, maxLoot);
        lootTable.save();
        // then create and add loot entries
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            TLootTableEntry tableEntry = new TLootTableEntry();
            tableEntry.setItem(RaidCraft.getItemIdString(item));
            tableEntry.setChance((int) ((1. / (double) itemCount) * 100.));
            tableEntry.setLootTable(tLootTable);
            plugin.getDatabase().save(tableEntry);
            DatabaseLootTableEntry entry = new DatabaseLootTableEntry(tableEntry);
            tableEntries.add(entry);
        }
        if (alias != null && alias.equals("")) {
            TLootTableAlias lootAlias = new TLootTableAlias();
            lootAlias.setTableAlias(alias);
            lootAlias.setLootTable(tLootTable);
            plugin.getDatabase().save(lootAlias);
        }
        lootTable.setEntries(tableEntries);
        lootTable.save();
        // add the loot table to the cache
        plugin.getLootTableManager().addTable(lootTable);
        return lootTable;
    }

    public LootTable createLootTable(ItemStack[] items, int minLoot, int maxLoot) {

        return createLootTable(null, items, minLoot, maxLoot);
    }

    public void createTreasureLootObject(UUID creator, Block block, int rewardLevel) {

        createTreasureLootObject(creator, block, rewardLevel, false);
    }

    public void createTreasureLootObject(UUID creator, Block block, int rewardLevel, boolean chat) {

        Player player = Bukkit.getPlayer(creator);
        LootObject existingLootObject = lootObjectStorage.getLootObject(block.getLocation());
        if (existingLootObject != null) {
            if (player != null && chat) {
                LootChat.alreadyLootObject(player);
            }
            return;
        }


        SimpleTreasureLootObject treasureLootObject = new SimpleTreasureLootObject();

        try {
            LootTable lootTable = plugin.getLootTableManager().getTable(TreasureRewardLevel.getLinkedTable(rewardLevel));
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
        lootObjectStorage.registerLootObject(treasureLootObject);

        if (player != null && chat) {
            LootChat.successfullyCreatedLootObject(player, treasureLootObject);
        }
    }

    public void createTimedLootObject(UUID creator, Block block, ItemStack[] items, int cooldown, int drops) {

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
        lootObjectStorage.registerLootObject(timedLootObject);
    }

    public void createDefaultLootObject(UUID creator, Block block, ItemStack[] items, int drops) {

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
        lootObjectStorage.registerLootObject(lootObject);
    }

    public void createPublicLootObject(UUID creator, Block block, ItemStack[] items, int cooldown) {
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
        lootObjectStorage.registerLootObject(publicLootObject);
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
}
