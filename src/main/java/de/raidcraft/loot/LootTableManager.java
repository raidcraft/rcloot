package de.raidcraft.loot;

import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.tables.ConfiguredRDSTable;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.exceptions.LootTableNotExistsException;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.loottables.LevelDependantLootTable;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableAlias;
import de.raidcraft.util.CaseInsensitiveMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootTableManager {

    private final LootPlugin plugin;
    private final Map<Integer, LootTable> cachedTables = new HashMap<>();
    private final Map<String, Map<Integer, LootTable>> levelDependantTables = new CaseInsensitiveMap<>();
    private final Map<String, RandomLootTableConfig> randomLootTableConfigs = new CaseInsensitiveMap<>();
    private final Map<String, Integer> aliasTables = new CaseInsensitiveMap<>();
    private final List<ConfiguredRDSTable> queuedTables = new ArrayList<>();

    protected LootTableManager(LootPlugin plugin) {

        this.plugin = plugin;
        load();
    }

    public void load() {

        List<TLootTable> list = plugin.getDatabase().find(TLootTable.class).findList();
        for (TLootTable table : list) {
            DatabaseLootTable lootTable = new DatabaseLootTable(table);
            if (plugin.getDatabase().find(TLootTable.class, table.getId()) != null) {
                addTable(lootTable);
            }
        }
        // lets also load all our configured random loot tables
        File path = new File(plugin.getDataFolder(), "random-tables");
        path.mkdirs();
        for (File file : path.listFiles()) {
            String name = file.getName().replace(".yml", "");
            levelDependantTables.put(name, new HashMap<>());
            randomLootTableConfigs.put(name, plugin.configure(new RandomLootTableConfig(plugin, file), false));
        }
        // TODO: remove the other loot table code and replace with this
        // lets test the new loot table loading system
        File lootTablesPath = new File(plugin.getDataFolder(), "loot-tables");
        loadLootTables(lootTablesPath, "");
        // initiate the loading process for all tables after they were loaded
        // tables can reference other tables so this needs to happen after loading all files
        queuedTables.forEach(ConfiguredRDSTable::load);
        queuedTables.clear();
    }

    private void loadLootTables(File path, String base) {

        File[] files = path.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadLootTables(path, base + file.getName().toLowerCase() + ".");
            } else if (file.getName().endsWith(".yml")) {
                ConfiguredRDSTable table = new ConfiguredRDSTable(base + file.getName().replace(".yml", ""),
                        plugin.configure(new SimpleConfiguration<>(plugin, file)));
                RDS.registerTable(plugin, table);
                queuedTables.add(table);
            }
        }
    }

    public void reload() {

        cachedTables.clear();
        aliasTables.clear();
        randomLootTableConfigs.clear();
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

    public LootTable getLevelDependantLootTable(String name, int level) throws LootTableNotExistsException {

        if (!randomLootTableConfigs.containsKey(name)) {
            throw new LootTableNotExistsException("The random level loot table " + name + " does not exist!");
        }
        if (levelDependantTables.get(name).containsKey(level)) {
            return levelDependantTables.get(name).get(level);
        }
        LevelDependantLootTable lootTable = new LevelDependantLootTable(randomLootTableConfigs.get(name), level);
        levelDependantTables.get(name).put(level, lootTable);
        return lootTable;
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