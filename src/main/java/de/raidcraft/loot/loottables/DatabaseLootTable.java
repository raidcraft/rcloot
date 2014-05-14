package de.raidcraft.loot.loottables;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.AbstractLootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.api.table.LootTableQuality;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableEntry;
import de.raidcraft.loot.tables.TLootTableQuality;

/**
 * Author: Philip
 * Date: 11.10.12 - 20:18
 * Description:
 */
public class DatabaseLootTable extends AbstractLootTable {

    public DatabaseLootTable(TLootTable lootTable) {

        super(lootTable.getId());
        load(lootTable);
    }

    public DatabaseLootTable(int id) {

        super(id);
    }

    private void load(TLootTable table) {

        setMinLootItems(table.getMinLoot());
        setMaxLootItems(table.getMaxLoot());
        if (table.getLootTableEntries().size() < 1) {
            delete();
            return;
        }
        // lets load up all our sub tables
        for (TLootTableEntry entry : table.getLootTableEntries()) {
            addEntry(new DatabaseLootTableEntry(entry));
        }
        // and qualities
        for (TLootTableQuality quality : table.getLootTableQualities()) {
            addQuality(new DatabaseLootTableQuality(quality));
        }
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        TLootTable table = database.find(TLootTable.class, getId());
        table.setMinLoot(getMinLootItems());
        table.setMaxLoot(getMaxLootItems());
        database.save(table);
        // save all our loot entries
        for (LootTableEntry entry : getEntries()) {
            entry.save();
        }
        // save our quality entries
        for (LootTableQuality quality : getQualities()) {
            quality.save();
        }
    }

    @Override
    public void delete() {

        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        TLootTable tLootTable = database.find(TLootTable.class, getId());
        RaidCraft.getComponent(LootPlugin.class).getLogger().info(
            "deleted loot table (" + tLootTable.getId() + ")");
        database.delete(tLootTable);
    }
}