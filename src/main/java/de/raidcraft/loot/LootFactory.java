package de.raidcraft.loot;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.database.tables.LootTablesTable;
import de.raidcraft.loot.exceptions.LootTableNotExistsException;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.object.SimpleLootObject;
import de.raidcraft.loot.object.SimpleTimedLootObject;
import de.raidcraft.loot.object.SimpleTreasureLootObject;
import de.raidcraft.loot.object.TreasureLootObject;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.table.SimpleLootTable;
import de.raidcraft.loot.table.SimpleLootTableEntry;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.TreasureRewardLevel;
import org.bukkit.Bukkit;
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
    public final static LootFactory inst = new LootFactory();
    private static Map<Block, LootObject> lootObjects = new HashMap<>();

    public void deleteLootObject(LootObject lootObject, boolean andTable) {

        RaidCraft.getTable(LootObjectsTable.class).deleteObject(lootObject);

        if (andTable && !(lootObject instanceof TreasureLootObject)) {
            RaidCraft.getTable(LootTablesTable.class).deleteTable(lootObject.getLootTable());
        }

        unregisterLootObject(lootObject);
    }

    public LootObject getLootObject(Block block) {

        if (lootObjects.containsKey(block)) {
            return lootObjects.get(block);
        }
        return lootObjects.get(block);
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
        LootObject existingLootObject = LootFactory.inst.getLootObject(block);
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
            CommandBook.logger().warning("[Loot] Try to assign non existing loot table (treasure object creation)!");
            if (player != null && chat) {
                LootChat.failureDuringCreation(player);
            }
            return;
        }

        treasureLootObject.setHost(block);
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
        timedLootObject.setHost(block);
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
        lootObject.setHost(block);
        lootObject.assignLootTable(lootTable);
        lootObject.setCreator(creator);
        lootObject.setCreated(System.currentTimeMillis() / 1000);
        lootObject.setEnabled(true);

        // save loot object in database
        RaidCraft.getTable(LootObjectsTable.class).addObject(lootObject);

        // register loot object in cache
        addLootObject(lootObject);
    }

    public void addLootObject(LootObject lootObject) {

        if (lootObjects.containsKey(lootObject.getHost())) {
            return;
        }
        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHost());
        if (otherChestBlock != null) {
            if (lootObjects.containsKey(otherChestBlock)) {
                return;
            }
            lootObjects.put(otherChestBlock, lootObject);
        }

        lootObjects.put(lootObject.getHost(), lootObject);
    }

    public void unregisterLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHost());
        if (otherChestBlock != null) {
            lootObjects.remove(otherChestBlock);
        }
        lootObjects.remove(lootObject.getHost());
    }

    public void loadLootObjects() {

        lootObjects.clear();
        for (LootObject lootObject : RaidCraft.getTable(LootObjectsTable.class).getAllObjects()) {
            addLootObject(lootObject);
        }
    }
}
