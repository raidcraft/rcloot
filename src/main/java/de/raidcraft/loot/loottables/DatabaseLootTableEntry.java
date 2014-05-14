package de.raidcraft.loot.loottables;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.AbstractLootTableEntry;
import de.raidcraft.loot.tables.TLootTableEntry;

/**
 * @author Silthus
 */
public class DatabaseLootTableEntry extends AbstractLootTableEntry {

    public DatabaseLootTableEntry(TLootTableEntry entry) {

        super(entry.getId());
        setItem(entry.getItem());
        setAmount(entry.getAmount());
        setChance(entry.getChance());
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        TLootTableEntry entry = database.find(TLootTableEntry.class, getId());
        entry.setChance(getChance());
        entry.setItem(RaidCraft.getItemIdString(getItem()));
        entry.setAmount(getAmount());
        database.save(entry);
    }
    
    public void delete() {
        
        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        TLootTableEntry entry = database.find(TLootTableEntry.class, getId());
        RaidCraft.getComponent(LootPlugin.class).getLogger().info(
            "deleted loot table (" + entry.getLootTable().getId() + ") entry: " + getId() + " - " + entry.getItem());
        database.delete(entry);
    }
}