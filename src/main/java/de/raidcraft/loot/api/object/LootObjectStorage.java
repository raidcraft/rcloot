package de.raidcraft.loot.api.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.util.ChestDispenserUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootObjectStorage implements Component {

    private Map<Location, LootObject> sortedObjects = new HashMap<>();

    public LootObjectStorage() {
        RaidCraft.registerComponent(LootObjectStorage.class, this);
    }

    public void unregisterLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHostLocation().getBlock());
        if (otherChestBlock != null) {
            removeLootObjectHost(otherChestBlock.getLocation());
        }
        removeLootObjectHost(lootObject.getHostLocation());
    }

    public void registerLootObject(LootObject lootObject) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(lootObject.getHostLocation().getBlock());
        if (otherChestBlock != null) {
            addLootObjectHost(otherChestBlock.getLocation(), lootObject);
        }
        addLootObjectHost(lootObject.getHostLocation(), lootObject);
    }

    public LootObject getLootObject(Location location) {

        LootObject lootObject = sortedObjects.get(location);

        Block otherBlock = ChestDispenserUtil.getOtherChestBlock(location.getBlock());
        if (otherBlock != null) {
            if (lootObject != null) {
                addLootObjectHost(otherBlock.getLocation(), lootObject);
            }
            lootObject = sortedObjects.get(otherBlock.getLocation());
        }

        return lootObject;
    }

    private void addLootObjectHost(Location hostLocation, LootObject lootObject) {

        sortedObjects.put(hostLocation, lootObject);
    }

    private void removeLootObjectHost(Location hostLocation) {

        sortedObjects.remove(hostLocation);
    }

    public void reload() {

        sortedObjects.clear();
        List<TLootObject> objects = RaidCraft.getDatabase(LootPlugin.class).find(TLootObject.class).findList();
        for (TLootObject dbEntry : objects) {
            DatabaseLootObject lootObject = new DatabaseLootObject(dbEntry);
            addLootObjectHost(lootObject.getHostLocation(), lootObject);
        }
        RaidCraft.LOGGER.info("[RCLoot] Es wurden " + objects.size() + " Loot-Objekte in den Cache geladen!");
    }
}
