package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.loottables.LevelDependantLootTable;
import de.raidcraft.loot.loottables.QueuedTable;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableAlias;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author Philip Urban
 */
public class LootTableManager implements Component {

    private final LootPlugin plugin;
    private final Map<Integer, LootTable> cachedTables = new HashMap<>();
    private final Map<String, Map<Integer, RDSTable>> levelDependantTables = new CaseInsensitiveMap<>();
    private final Map<String, Integer> aliasTables = new CaseInsensitiveMap<>();
    private final List<QueuedTable> queuedTables = new ArrayList<>();

    protected LootTableManager(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootTableManager.class, this);
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
        ConfigUtil.loadRecursiveConfigs(plugin, "loot-tables", new ConfigLoader(plugin) {
            @Override
            public void loadConfig(String id, ConfigurationSection config) {
                Optional<RDSObject> object = RDS.createObject(config.getString("type", "table"), config, false);
                if (!object.isPresent()) {
                    plugin.getLogger().warning("Could not find loot table with type: "
                            + config.getString("type", "table") + " in " + ConfigUtil.getFileName(config));
                    return;
                }
                RDSObject rdsObject = object.get();
                if (!(rdsObject instanceof RDSTable)) {
                    plugin.getLogger().warning(ConfigUtil.getFileName(config) + " is not a loot table!");
                    return;
                }
                RDSTable table = (RDSTable) rdsObject;
                RDS.registerTable(plugin, id, table, config);
                ConfigurationSection args = config.getConfigurationSection("args");
                if (args != null) {
                    queuedTables.add(new QueuedTable(table, args));
                }
            }
        });
        // initiate the loading process for all tables after they were loaded
        // tables can reference other tables so this needs to happen after loading all files
        queuedTables.forEach(QueuedTable::load);
        queuedTables.clear();
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
            LevelDependantLootTable lootTable = ((LevelDependantLootTable) table.get()).createInstance();
            levelDependantTables.get(name).put(level, lootTable);
            updateLevelDependantTables(lootTable, level);
            return lootTable;
        }
        if (table.isPresent()) {
            updateLevelDependantTables(table.get(), level);
            return table.get();
        }
        return null;
    }

    private void updateLevelDependantTables(RDSTable table, int level) {

        if (table instanceof LevelDependantLootTable) {
            ((LevelDependantLootTable) table).setLevel(level);
            ((LevelDependantLootTable) table).loadItems();
        }
        for (RDSObject object : table.getContents()) {
            if (object instanceof LevelDependantLootTable) {
                ((LevelDependantLootTable) object).setLevel(level);
                ((LevelDependantLootTable) object).loadItems();
            } else if (object instanceof RDSTable) {
                updateLevelDependantTables((RDSTable) object, level);
            }
        }
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

    public String getAlias(int lootTableId) {
        for (Map.Entry<String, Integer> entry : aliasTables.entrySet()) {
            if (entry.getValue() == lootTableId) return entry.getKey();
        }
        return null;
    }
}