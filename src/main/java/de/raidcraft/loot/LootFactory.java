package de.raidcraft.loot;

import de.raidcraft.componentutils.database.Database;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.database.tables.LootTablesTable;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.object.SimpleLootObject;
import de.raidcraft.loot.object.SimpleTimedLootObject;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.table.SimpleLootTable;
import de.raidcraft.loot.table.SimpleLootTableEntry;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
        
        Database.getTable(LootObjectsTable.class).deleteObject(lootObject);

        if(andTable) {
            Database.getTable(LootTablesTable.class).deleteTable(lootObject.getLootTable());
        }
        
        unregisterLootObject(lootObject);
    }

    public LootObject getLootObject(Block block) {
        if(lootObjects.containsKey(block)) {
            return lootObjects.get(block);
        }
        return lootObjects.get(block);
    }
    
    private LootTable createLootTable(ItemStack[] items, int minLoot, int maxLoot) {
        List<LootTableEntry> tableEntries = new ArrayList<>();
        // at first, count total items
        int itemCount = 0;
        for(ItemStack item : items) {
            if(item != null) {
                itemCount++;
            }
        }
        // then create and add loot entries
        for(ItemStack item : items) {
            if(item == null) {
                continue;
            }
            LootTableEntry tableEntry = new SimpleLootTableEntry();
            tableEntry.setItem(item);
            tableEntry.setChance((int)((1./(double)itemCount)*100.));
            tableEntries.add(tableEntry);
        }
        LootTable lootTable = new SimpleLootTable();
        lootTable.setEntries(tableEntries);
        lootTable.setMinMaxLootItems(minLoot, maxLoot);
        return lootTable;
    }
    
    public void createTimedLootObject(String creator, Block block, ItemStack[] items, int cooldown, int drops) {
        int itemCount = 0;
        for(ItemStack item : items) {
            if(item != null) {
                itemCount++;
            }
        }
        if(drops != SettingStorage.ALL) {
            itemCount = drops;
        }
        LootTable lootTable = createLootTable(items, itemCount, itemCount);
        // create loot object
        SimpleTimedLootObject timedLootObject = new SimpleTimedLootObject();
        timedLootObject.setCooldown(cooldown);
        timedLootObject.setHost(block);
        timedLootObject.assignLootTable(lootTable);
        timedLootObject.setCreator(creator);
        timedLootObject.setCreated(System.currentTimeMillis());
        timedLootObject.setEnabled(true);

        // save loot object in database
        Database.getTable(LootObjectsTable.class).addObject(timedLootObject);

        // register loot object in cache
        addLootObject(timedLootObject);
    }

    public void createDefaultLootObject(String creator, Block block, ItemStack[] items, int drops) {
        int itemCount = 0;
        for(ItemStack item : items) {
            if(item != null) {
                itemCount++;
            }
        }
        if(drops != SettingStorage.ALL) {
            itemCount = drops;
        }
        LootTable lootTable = createLootTable(items, itemCount, itemCount);
        // create loot object
        SimpleLootObject lootObject = new SimpleLootObject();
        lootObject.setHost(block);
        lootObject.assignLootTable(lootTable);
        lootObject.setCreator(creator);
        lootObject.setCreated(System.currentTimeMillis());
        lootObject.setEnabled(true);

        // save loot object in database
        Database.getTable(LootObjectsTable.class).addObject(lootObject);

        // register loot object in cache
        addLootObject(lootObject);
    }

    public void addLootObject(LootObject lootObject) {
        if(lootObject.getHost().getType() == Material.CHEST) {
            if(lootObject.getHost().getRelative(1, 0, 0).getType() == Material.CHEST) lootObjects.put(lootObject.getHost().getRelative(1, 0, 0), lootObject);
            if(lootObject.getHost().getRelative(-1, 0, 0).getType() == Material.CHEST) lootObjects.put(lootObject.getHost().getRelative(-1, 0, 0), lootObject);
            if(lootObject.getHost().getRelative(0, 0, 1).getType() == Material.CHEST) lootObjects.put(lootObject.getHost().getRelative(0, 0, 1), lootObject);
            if(lootObject.getHost().getRelative(0, 0, -1).getType() == Material.CHEST) lootObjects.put(lootObject.getHost().getRelative(0, 0, -1), lootObject);
        }
        lootObjects.put(lootObject.getHost(), lootObject);
    }

    public void unregisterLootObject(LootObject lootObject) {
        if(lootObject.getHost().getType() == Material.CHEST) {
            if(lootObject.getHost().getRelative(1, 0, 0).getType() == Material.CHEST) lootObjects.remove(lootObject.getHost().getRelative(1, 0, 0));
            if(lootObject.getHost().getRelative(-1, 0, 0).getType() == Material.CHEST) lootObjects.remove(lootObject.getHost().getRelative(-1, 0, 0));
            if(lootObject.getHost().getRelative(0, 0, 1).getType() == Material.CHEST) lootObjects.remove(lootObject.getHost().getRelative(0, 0, 1));
            if(lootObject.getHost().getRelative(0, 0, -1).getType() == Material.CHEST) lootObjects.remove(lootObject.getHost().getRelative(0, 0, -1));
        }
        lootObjects.remove(lootObject.getHost());
    }

    public void loadLootObjects() {
        lootObjects.clear();
        for(LootObject lootObject : Database.getTable(LootObjectsTable.class).getAllObjects()) {
            addLootObject(lootObject);
        }
    }
}
