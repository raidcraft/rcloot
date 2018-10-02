package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.api.object.DatabaseLootObject;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.object.LootObjectStorage;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 18.10.12 - 20:57
 * Description:
 */
public class LootFactory implements Component {

    public final static UUID ANY = UUID.fromString("000000f5-2100-41cc-a05d-3ed7da445841");
    public final static UUID AutomaticPlacerSurface = UUID.fromString("000000ef-b1a0-4173-9775-e5c1352a0cf9");
    public final static UUID AutomaticPlacerCave = UUID.fromString("0000008f-00ae-4368-bb33-b6c965a1f3a3");

    private LootObjectStorage lootObjectStorage;
    private LootPlugin plugin;

    public LootFactory(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootFactory.class, this
        );
        lootObjectStorage = plugin.getLootObjectStorage();
    }

    public void deleteLootObject(LootObject lootObject) {

        lootObject.delete();

        lootObjectStorage.unregisterLootObject(lootObject);
    }

    public LootObject createLootObject(Block block, RDSTable table) {
        Objects.requireNonNull(block);
        Objects.requireNonNull(table);

        DatabaseLootObject object = new DatabaseLootObject(block, table);
        object.setHostLocation(block.getLocation());

        object.save();

        // register loot object in cache
        lootObjectStorage.registerLootObject(object);

        return object;
    }

    public String getObjectInfo(LootObject lootObject) {

        return lootObject.toString();
    }
}
