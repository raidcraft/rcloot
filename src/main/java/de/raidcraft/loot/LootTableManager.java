package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.random.NamedRDSTable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.loottables.LevelDependantLootTable;
import de.raidcraft.loot.loottables.QueuedTable;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.ConfigUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * @author Philip Urban
 */
public class LootTableManager implements Component {

    private final LootPlugin plugin;
    private final Map<String, Map<Integer, RDSTable>> levelDependantTables = new CaseInsensitiveMap<>();
    private final List<QueuedTable> queuedTables = new ArrayList<>();

    protected LootTableManager(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootTableManager.class, this);
    }

    public void load() {

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
                queuedTables.add(new QueuedTable(table, config));
            }
        });
        // initiate the loading process for all tables after they were loaded
        // tables can reference other tables so this needs to happen after loading all files
        queuedTables.forEach(QueuedTable::load);
        queuedTables.clear();
    }

    public void reload() {

        levelDependantTables.clear();
        RDS.unregisterTables(plugin);
        load();
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

    public Collection<RDSTable> getTables() {
        return RDS.getLootTables();
    }
}