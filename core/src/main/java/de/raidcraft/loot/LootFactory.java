package de.raidcraft.loot;

import de.faldoria.loot.api.LootObject;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.lootobjects.DatabaseLootObject;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.util.LocationUtil;
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

    private LootPlugin plugin;

    public LootFactory(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootFactory.class, this);
    }

    public LootObject createLootObject(Block block, RDSTable table) {
        Objects.requireNonNull(block);
        Objects.requireNonNull(table);

        TLootObject existingObject = TLootObject.find.byLocation(block.getLocation());
        if (existingObject != null) {
            existingObject.deletePermanent();
        }

        DatabaseLootObject object = new DatabaseLootObject(block, table);
        object.save();

        return object;
    }

    public LootObject createLootObject(TLootObject entry) {
        if (entry == null) return null;
        if (LocationUtil.getCaseInsensitiveWorld(entry.getWorld()) == null) {
            return null;
        }

        return new DatabaseLootObject(entry);
    }

    public String getObjectInfo(LootObject lootObject) {

        return lootObject.toString();
    }
}
