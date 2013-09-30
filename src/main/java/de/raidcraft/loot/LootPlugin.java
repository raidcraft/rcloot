package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.commands.LootCommands;
import de.raidcraft.loot.database.tables.*;
import de.raidcraft.loot.listener.BlockListener;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.loothost.LootHostManager;
import de.raidcraft.loot.loothost.hosts.ChestHost;
import de.raidcraft.loot.loothost.hosts.DispenserHost;
import de.raidcraft.loot.loothost.hosts.DropperHost;
import de.raidcraft.loot.loothost.hosts.TrappedChestHost;
import de.raidcraft.loot.object.LootObjectStorage;
import de.raidcraft.loot.table.LootTableManager;
import de.raidcraft.loot.util.TreasureRewardLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:51
 * Description:
 */
public class LootPlugin extends BasePlugin implements Component {

    public LocalConfiguration config;

    private LootFactory lootFactory;
    private LootObjectStorage lootObjectStorage;
    private LootTableManager lootTableCache;
    private LootHostManager lootHostManager;

    @Override
    public void enable() {

        registerTable(LootObjectsTable.class, new LootObjectsTable());
        registerTable(LootPlayersTable.class, new LootPlayersTable());
        registerTable(LootTableEntriesTable.class, new LootTableEntriesTable());
        registerTable(LootTablesTable.class, new LootTablesTable());

        registerCommands(LootCommands.class);
        registerEvents(new PlayerListener());
        registerEvents(new BlockListener());

        lootObjectStorage = new LootObjectStorage();
        lootFactory = new LootFactory(this);
        lootTableCache = new LootTableManager();
        lootHostManager = new LootHostManager(this);

        // register all default hosts
        lootHostManager.registerLootHost(new ChestHost());
        lootHostManager.registerLootHost(new TrappedChestHost());
        lootHostManager.registerLootHost(new DropperHost());
        lootHostManager.registerLootHost(new DispenserHost());

        // load all tables in cache
        RaidCraft.getTable(LootTablesTable.class).getAllLootTables();

        // register auto chest placer
//        new AutomaticPlacer();

//        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
//            @Override
//            public void run() {
//                AutomaticPlacer.INST.resume();
//            }
//        }, 10*20);

        reload();
    }

    @Override
    public void disable() {

//        AutomaticPlacer.INST.save();
    }

    @Override
    public void reload() {

        loadConfig();
        lootObjectStorage.reload();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> databases = new ArrayList<>();
        databases.add(TLootTableAlias.class);

        return databases;
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

    public LootFactory getLootFactory() {

        return lootFactory;
    }

    public LootObjectStorage getLootObjectStorage() {

        return lootObjectStorage;
    }

    public LootTableManager getLootTableCache() {

        return lootTableCache;
    }

    public LootHostManager getLootHostManager() {

        return lootHostManager;
    }
}
