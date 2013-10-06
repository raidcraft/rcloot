package de.raidcraft.loot.table;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.tables.TLootTableAlias;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootTableManager {

    private Map<Integer, LootTable> cachedTables = new HashMap<>();

    public void addTable(LootTable table) {

        cachedTables.put(table.getId(), table);
    }

    public void clearCache() {

        cachedTables.clear();
    }

    public LootTable getTable(int id) {

        return cachedTables.get(id);
    }

    public LootTable getTable(String alias) {

        TLootTableAlias tLootTableAlias = RaidCraft.getDatabase(LootPlugin.class)
                .find(TLootTableAlias.class).where().ieq("alias", alias).findUnique();
        if(tLootTableAlias != null) {
            return getTable(tLootTableAlias.getLootTable().getId());
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