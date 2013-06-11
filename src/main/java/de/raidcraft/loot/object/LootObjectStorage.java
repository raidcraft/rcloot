package de.raidcraft.loot.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.util.ChestDispenserUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootObjectStorage {

    private Map<String, Map<Integer, Map<Integer, Map<Integer, LootObject>>>> sortedObjects = new HashMap<>();

    public void registerLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHostLocation().getBlock());
        if (otherChestBlock != null) {
            removeLootObjectHost(otherChestBlock.getLocation(), lootObject);
        }
        removeLootObjectHost(lootObject.getHostLocation(), lootObject);
    }

    public void unregisterLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHostLocation().getBlock());
        if (otherChestBlock != null) {
            addLootObjectHost(otherChestBlock.getLocation(), lootObject);
        }
        addLootObjectHost(lootObject.getHostLocation(), lootObject);
    }

    public LootObject getLootObject(Location location) {

        String worldName = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        if(sortedObjects.containsKey(worldName)) {
            if(sortedObjects.get(worldName).containsKey(x)) {
                if(sortedObjects.get(worldName).get(x).containsKey(y)) {
                    if(sortedObjects.get(worldName).get(x).get(y).containsKey(z)) {
                        return sortedObjects.get(worldName).get(x).get(y).get(z);
                    }
                }
            }
        }
        return null;
    }

    private void addLootObjectHost(Location hostLocation, LootObject lootObject) {

        String worldName = lootObject.getHostLocation().getWorld().getName();
        int x = hostLocation.getBlockX();
        int y = hostLocation.getBlockY();
        int z = hostLocation.getBlockZ();

        if(!sortedObjects.containsKey(worldName)) {
            sortedObjects.put(worldName, new HashMap<Integer, Map<Integer, Map<Integer, LootObject>>>());
        }

        if(!sortedObjects.get(worldName).containsKey(x)) {
            sortedObjects.get(worldName).put(x, new HashMap<Integer, Map<Integer, LootObject>>());
        }

        if(!sortedObjects.get(worldName).get(x).containsKey(y)) {
            sortedObjects.get(worldName).get(x).put(y, new HashMap<Integer, LootObject>());
        }

        sortedObjects.get(worldName).get(x).get(y).put(z, lootObject);
    }

    private void removeLootObjectHost(Location hostLocation, LootObject lootObject) {

        String worldName = lootObject.getHostLocation().getWorld().getName();
        int x = hostLocation.getBlockX();
        int y = hostLocation.getBlockY();
        int z = hostLocation.getBlockZ();

        if(!sortedObjects.containsKey(worldName)) {
            return;
        }

        if(!sortedObjects.get(worldName).containsKey(x)) {
            return;
        }

        if(!sortedObjects.get(worldName).get(x).containsKey(y)) {
            return;
        }

        if(!sortedObjects.get(worldName).get(x).get(y).containsKey(z)) {
            return;
        }

        sortedObjects.get(worldName).get(x).get(y).remove(z);
    }

    public void reload() {

        sortedObjects.clear();
        List<LootObject> lootObjects = RaidCraft.getTable(LootObjectsTable.class).getAllObjects();
        RaidCraft.LOGGER.info("[RCLoot] Es wurden " + lootObjects.size() + " in den Cache geladen!");
    }
}
