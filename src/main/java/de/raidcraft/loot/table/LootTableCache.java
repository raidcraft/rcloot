package de.raidcraft.loot.table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootTableCache {

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

}
