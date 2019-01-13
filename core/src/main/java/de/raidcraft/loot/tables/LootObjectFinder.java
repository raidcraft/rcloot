package de.raidcraft.loot.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.util.CustomItemUtil;
import io.ebean.EbeanServer;
import io.ebean.Finder;
import io.ebean.Query;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;

public class LootObjectFinder extends Finder<Integer, TLootObject> {

    private EbeanServer database;

    protected LootObjectFinder() {
        super(TLootObject.class);
    }

    private EbeanServer getDatabase() {
        if (database == null) {
            database = RaidCraft.getDatabase(LootPlugin.class);
        }
        return database;
    }

    @Override
    public Query<TLootObject> query() {
        return getDatabase().find(TLootObject.class);
    }

    @Nullable
    @Override
    public TLootObject byId(Integer id) {
        return getDatabase().find(TLootObject.class, id);
    }

    public TLootObject byLocation(Location location) {
        if (location == null) return null;
        return query().where()
                .eq("x", location.getBlockX())
                .eq("y", location.getBlockY())
                .eq("z", location.getBlockZ())
                .eq("world", location.getWorld().getName())
                .findOne();
    }

    public TLootObject byBlock(Block block) {

        if (block == null) return null;
        return byLocation(block.getLocation());
    }

    public TLootObject byInventory(Inventory inventory) {
        try {
            if (inventory == null) return null;
            return byId(CustomItemUtil.decodeItemId(inventory.getName()));
        } catch (CustomItemException e) {
            return null;
        }
    }
}
