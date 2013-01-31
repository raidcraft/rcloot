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
import de.raidcraft.loot.util.TreasureRewardLevel;
import org.bukkit.Bukkit;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:51
 * Description:
 */
public class LootPlugin extends BasePlugin implements Component {

    public LocalConfiguration config;
    private int reloadTaskId;

    @Override
    public void enable() {

        reloadTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {

                loadConfig();

                registerTable(LootObjectsTable.class, new LootObjectsTable());
                registerTable(LootPlayersTable.class, new LootPlayersTable());
                registerTable(LootTableEntriesTable.class, new LootTableEntriesTable());
                registerTable(LootTablesTable.class, new LootTablesTable());

                // do some command init
                registerCommands(LootCommands.class);
                // and of course we need some event handlers
                registerEvents(new PlayerListener());
                registerEvents(new BlockListener());

                LootFactory.inst.loadLootObjects(); // loads all existing loot objects from database
                getLogger().info("[Loot] Found DB connection, init loot module...");
                Bukkit.getScheduler().cancelTask(reloadTaskId);
            }
        }, 0, 2 * 20);
    }

    @Override
    public void disable() {


    }

    @Override
    public void reload() {

        config.reload();
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
}
