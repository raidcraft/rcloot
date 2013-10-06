package de.raidcraft.loot;

import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableAlias;
import de.raidcraft.util.CaseInsensitiveMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootTableManager {

    private final LootPlugin plugin;
    private final Map<Integer, LootTable> cachedTables = new HashMap<>();
    private final Map<String, Integer> aliasTables = new CaseInsensitiveMap<>();

    protected LootTableManager(LootPlugin plugin) {

        this.plugin = plugin;
        load();
    }

    public void load() {

        List<TLootTable> list = plugin.getDatabase().find(TLootTable.class).findList();
        for (TLootTable table : list) {
            addTable(new DatabaseLootTable(table));
        }
    }

    public void reload() {

        cachedTables.clear();
        load();
    }

    public void addTable(LootTable table) {

        cachedTables.put(table.getId(), table);
        TLootTableAlias alias = plugin.getDatabase().find(TLootTable.class, table.getId()).getAlias();
        if (alias != null) {
            aliasTables.put(alias.getAlias(), table.getId());
        }
    }

    public LootTable getTable(int id) {

        return cachedTables.get(id);
    }

    public LootTable getTable(String alias) {

        if (aliasTables.containsKey(alias)) {
            return getTable(aliasTables.get(alias));
        }
        return null;
    }

    public String getIdStringList() {

        String list = "0";
        for(int id : cachedTables.keySet()) {
            if(list.length() > 0) {
                list += ",";
            }
            list += id;
        }
        return list;
    }
}