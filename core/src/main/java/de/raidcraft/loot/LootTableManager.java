package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigLoader;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.loottables.QueuedTable;
import de.raidcraft.util.ConfigUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Philip Urban
 */
public class LootTableManager implements Component {

    private final LootPlugin plugin;

    protected LootTableManager(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootTableManager.class, this);
        Quests.registerQuestLoader(new LootTableConfigLoader(plugin, "loot"));
    }

    public void load() {

        // lets test the new loot table loading system
        LootTableConfigLoader configLoader = new LootTableConfigLoader(plugin);
        ConfigUtil.loadRecursiveConfigs(plugin, "loot-tables", configLoader);
        configLoader.onLoadingComplete();
    }

    public void reload() {
        RDS.unregisterTables(plugin);
        load();
    }

    public Collection<RDSTable> getTables() {
        return RDS.getLootTables();
    }

    public class LootTableConfigLoader extends ConfigLoader {

        private final List<QueuedTable> queuedTables = new ArrayList<>();

        public LootTableConfigLoader(BasePlugin plugin) {
            super(plugin);
        }

        public LootTableConfigLoader(BasePlugin plugin, String suffix) {
            super(plugin, suffix);
        }

        @Override
        public void loadConfig(String id, ConfigurationBase config) {
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

        @Override
        public void unloadConfig(String id) {
            RDS.unregisterTable(plugin, id);
        }

        @Override
        public void onLoadingComplete() {
            // initiate the loading process for all tables after they were loaded
            // tables can reference other tables so this needs to happen after loading all files
            queuedTables.forEach(QueuedTable::load);
            queuedTables.clear();
        }
    }
}