package de.raidcraft.loot.api.object;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.tables.TLootTable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DatabaseLootObject extends AbstractLootObject {

    public DatabaseLootObject(TLootObject dbEntry, LootTable table) {
        this(dbEntry);
        setLootTable(table);
    }

    public DatabaseLootObject(TLootObject dbEntry) {
        super(dbEntry.getId());
        setHostLocation(new Location(Bukkit.getWorld(dbEntry.getWorld()), dbEntry.getX(), dbEntry.getY(), dbEntry.getZ()));
        setCooldown(dbEntry.getCooldown());
        setEnabled(dbEntry.isEnabled());
        setInfinite(dbEntry.isInfinite());
        setPublicLootObject(dbEntry.isPublicLootObject());
        setLootTable(new DatabaseLootTable(dbEntry.getLootTable()));
    }

    @Override
    public void save() {
        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        TLootObject lootObject = database.find(TLootObject.class, getId());
        if (lootObject == null) {
            lootObject = new TLootObject();
        }
        lootObject.setCooldown(getCooldown());
        lootObject.setEnabled(isEnabled());
        lootObject.setInfinite(isInfinite());
        lootObject.setPublicLootObject(isPublicLootObject());
        lootObject.setWorld(getHostLocation().getWorld().getName());
        lootObject.setX(getHostLocation().getBlockX());
        lootObject.setY(getHostLocation().getBlockY());
        lootObject.setZ(getHostLocation().getBlockZ());

        LootTable lootTable = getLootTable();
        if (lootTable != null) {
            lootObject.setLootTable(database.find(TLootTable.class, lootTable.getId()));
        }

        database.save(lootObject);
    }

    @Override
    public void delete() {
        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        database.delete(TLootObject.class, getId());
        RaidCraft.getComponent(LootPlugin.class).getLogger().info(
                "deleted loot object (" + getId() + ")");
    }
}
