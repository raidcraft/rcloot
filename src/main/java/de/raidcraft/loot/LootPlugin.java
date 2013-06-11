package de.raidcraft.loot;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.commands.LootCommands;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import de.raidcraft.loot.database.tables.LootTableEntriesTable;
import de.raidcraft.loot.database.tables.LootTablesTable;
import de.raidcraft.loot.listener.BlockListener;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.table.LootTableCache;
import de.raidcraft.loot.util.TreasureRewardLevel;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:51
 * Description:
 */
public class LootPlugin extends BasePlugin implements Component {

    public static LootPlugin INST;
    public LocalConfiguration config;

    private LootTableCache lootTableCache;

    @Override
    public void enable() {

        INST = this;
        loadConfig();
        registerTable(LootObjectsTable.class, new LootObjectsTable());
        registerTable(LootPlayersTable.class, new LootPlayersTable());
        registerTable(LootTableEntriesTable.class, new LootTableEntriesTable());
        registerTable(LootTablesTable.class, new LootTablesTable());

        registerCommands(LootCommands.class);
        registerEvents(new PlayerListener());
        registerEvents(new BlockListener());

        lootTableCache = new LootTableCache();

        // register auto chest placer
//        new AutomaticPlacer();

//        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
//            @Override
//            public void run() {
//                AutomaticPlacer.INST.resume();
//            }
//        }, 10*20);
    }

    @Override
    public void disable() {

//        AutomaticPlacer.INST.save();
    }

    @Override
    public void reload() {

        config.reload();
        lootTableCache.clearCache();
    }

    public void loadConfig() {

        config = configure(new LocalConfiguration(this));

        TreasureRewardLevel.addRewardLevel(1, config.rewardLevel1);
        TreasureRewardLevel.addRewardLevel(2, config.rewardLevel2);
        TreasureRewardLevel.addRewardLevel(3, config.rewardLevel3);
        TreasureRewardLevel.addRewardLevel(4, config.rewardLevel4);
        TreasureRewardLevel.addRewardLevel(5, config.rewardLevel5);
        TreasureRewardLevel.addRewardLevel(6, config.rewardLevel6);
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("reward-level-table-1")
        public int rewardLevel1 = 0;
        @Setting("reward-level-table-2")
        public int rewardLevel2 = 0;
        @Setting("reward-level-table-3")
        public int rewardLevel3 = 0;
        @Setting("reward-level-table-4")
        public int rewardLevel4 = 0;
        @Setting("reward-level-table-5")
        public int rewardLevel5 = 0;
        @Setting("reward-level-table-6")
        public int rewardLevel6 = 0;

        public LocalConfiguration(LootPlugin plugin) {

            super(plugin, "config.yml");
        }
    }

    public LootTableCache getLootTableCache() {

        return lootTableCache;
    }
}
