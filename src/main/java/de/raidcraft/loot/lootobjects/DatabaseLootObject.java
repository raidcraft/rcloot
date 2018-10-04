package de.raidcraft.loot.lootobjects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.tables.TLootObject;
import io.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class DatabaseLootObject extends AbstractLootObject {

    public DatabaseLootObject(Block block, RDSTable table) {
        setHostLocation(block.getLocation());
        setMaterial(block.getType());
        setLootTable(table);
    }

    public DatabaseLootObject(TLootObject dbEntry) {
        setId(dbEntry.getId());
        setHostLocation(new Location(Bukkit.getWorld(dbEntry.getWorld()), dbEntry.getX(), dbEntry.getY(), dbEntry.getZ()));
        setCooldown(dbEntry.getCooldown());
        setEnabled(dbEntry.isEnabled());
        setInfinite(dbEntry.isInfinite());
        setPublicLootObject(dbEntry.isPublicLootObject() || dbEntry.isDestroyable());
        setDestroyable(dbEntry.isDestroyable());
        setDestroyed(dbEntry.getDestroyed());
        setMaterial(Material.matchMaterial(dbEntry.getMaterial()));
        RDS.getTable(dbEntry.getLootTable()).ifPresent(this::setLootTable);
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
        lootObject.setPublicLootObject(isPublicLootObject() || isDestroyable());
        lootObject.setDestroyable(isDestroyable());
        lootObject.setDestroyed(getDestroyed());
        lootObject.setMaterial(getMaterial().name());
        lootObject.setWorld(getHostLocation().getWorld().getName());
        lootObject.setX(getHostLocation().getBlockX());
        lootObject.setY(getHostLocation().getBlockY());
        lootObject.setZ(getHostLocation().getBlockZ());

        RDSTable lootTable = getLootTable();
        if (lootTable != null) {
            lootTable.getId().ifPresent(lootObject::setLootTable);
        }

        database.save(lootObject);
        setId(lootObject.getId());
    }

    @Override
    public void delete() {
        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        database.delete(TLootObject.class, getId());
        RaidCraft.getComponent(LootPlugin.class).getLogger().info(
                "deleted loot object (" + getId() + ")");
    }
}
