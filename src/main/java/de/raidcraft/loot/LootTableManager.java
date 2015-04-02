package de.raidcraft.loot;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.api.random.tables.ConfiguredRDSTable;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.loottables.LevelDependantLootTable;
import de.raidcraft.loot.loottables.QueuedTable;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableAlias;
import de.raidcraft.util.CaseInsensitiveMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Philip Urban
 */
public class LootTableManager {

    private final LootPlugin plugin;
    private final Map<Integer, LootTable> cachedTables = new HashMap<>();
    private final Map<String, Map<Integer, RDSTable>> levelDependantTables = new CaseInsensitiveMap<>();
    private final Map<String, Integer> aliasTables = new CaseInsensitiveMap<>();
    private final List<QueuedTable> queuedTables = new ArrayList<>();

    protected LootTableManager(LootPlugin plugin) {

        this.plugin = plugin;
    }

    public void load() {

        List<TLootTable> list = plugin.getDatabase().find(TLootTable.class).findList();
        for (TLootTable table : list) {
            DatabaseLootTable lootTable = new DatabaseLootTable(table);
            if (plugin.getDatabase().find(TLootTable.class, table.getId()) != null) {
                addTable(lootTable);
            }
        }
        // lets test the new loot table loading system
        File lootTablesPath = new File(plugin.getDataFolder(), "loot-tables");
        loadLootTables(lootTablesPath, "");
        // initiate the loading process for all tables after they were loaded
        // tables can reference other tables so this needs to happen after loading all files
        queuedTables.forEach(QueuedTable::load);
        queuedTables.clear();
    }

    private void loadLootTables(File path, String base) {

        File[] files = path.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadLootTables(file, base + file.getName().toLowerCase() + ".");
            } else if (file.getName().endsWith(".yml")) {
                SimpleConfiguration<LootPlugin> config = plugin.configure(new SimpleConfiguration<>(plugin, file));
                RDSTable table = null;
                if (config.isSet("type")) {
                    Optional<RDSObjectFactory> creator = RDS.getObjectCreator(config.getString("type"));
                    if (creator.isPresent()) {
                        RDSObject rdsObject = creator.get().createInstance(config.getSafeConfigSection("args"));
                        if (rdsObject instanceof RDSTable) {
                            table = (RDSTable) rdsObject;
                        }
                    }
                } else {
                    table = new ConfiguredRDSTable();
                }
                if (table != null) {
                    RDS.registerTable(plugin, base + file.getName().replace(".yml", ""), table, config);
                    queuedTables.add(new QueuedTable(table, config));
                }
            }
        }
    }

    public void reload() {

        cachedTables.clear();
        aliasTables.clear();
        levelDependantTables.clear();
        RDS.unregisterTables(plugin);
        load();
    }

    public void addTable(LootTable table) {

        cachedTables.put(table.getId(), table);
        TLootTable tLootTable = plugin.getDatabase().find(TLootTable.class, table.getId());
        if (tLootTable == null) return;
        TLootTableAlias alias = tLootTable.getLootTableAlias();
        if (alias != null) {
            aliasTables.put(alias.getTableAlias(), table.getId());
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

    public RDSTable getLevelDependantLootTable(String name, int level) {

        if (levelDependantTables.containsKey(name) && levelDependantTables.get(name).containsKey(level)) {
            return levelDependantTables.get(name).get(level);
        }
        Optional<RDSTable> table = RDS.getTable(name);
        if (table.isPresent() && table.get() instanceof LevelDependantLootTable) {
            if (!levelDependantTables.containsKey(name)) {
                levelDependantTables.put(name, new HashMap<>());
            }
            LevelDependantLootTable lootTable = new LevelDependantLootTable(((LevelDependantLootTable) table.get()).getConfig(), level);
            levelDependantTables.get(name).put(level, lootTable);
            return lootTable;
        }
        if (table.isPresent()) {
            return table.get();
        }
        return null;
    }

    public String getIdStringList() {

        String list = "0";
        for (int id : cachedTables.keySet()) {
            if (list.length() > 0) {
                list += ",";
            }
            list += id;
        }
        return list;
    }
}