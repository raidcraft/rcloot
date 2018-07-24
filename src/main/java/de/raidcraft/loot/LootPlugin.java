package de.raidcraft.loot;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.api.random.RDS;
import de.raidcraft.loot.api.object.LootObjectStorage;
import de.raidcraft.loot.commands.LootCommands;
import de.raidcraft.loot.listener.BlockListener;
import de.raidcraft.loot.listener.InventoryListener;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.loothost.LootHostManager;
import de.raidcraft.loot.loothost.hosts.ChestHost;
import de.raidcraft.loot.loothost.hosts.DispenserHost;
import de.raidcraft.loot.loothost.hosts.DropperHost;
import de.raidcraft.loot.loothost.hosts.TrappedChestHost;
import de.raidcraft.loot.loottables.LevelDependantLootTable;
import de.raidcraft.loot.tables.*;
import de.raidcraft.loot.util.TreasureRewardLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:51
 * Description:
 */
public class LootPlugin extends BasePlugin implements Component {

    public LocalConfiguration config;

    @Getter
    private LootFactory lootFactory;
    @Getter
    private LootObjectStorage lootObjectStorage;
    @Getter
    private LootTableManager lootTableManager;
    @Getter
    private LootHostManager lootHostManager;
    @Getter
    private ToolbarManager toolbarManager;

    @Override
    public void enable() {

        loadConfig();

        registerCommands(LootCommands.class);
        registerEvents(new PlayerListener());
        registerEvents(new BlockListener());
        registerEvents(new InventoryListener(this));

        lootObjectStorage = new LootObjectStorage();
        lootFactory = new LootFactory(this);
        lootTableManager = new LootTableManager(this);
        lootHostManager = new LootHostManager(this);

        // register all default hosts
        lootHostManager.registerLootHost(new ChestHost());
        lootHostManager.registerLootHost(new TrappedChestHost());
        lootHostManager.registerLootHost(new DropperHost());
        lootHostManager.registerLootHost(new DispenserHost());

        RDS.registerObject(new LevelDependantLootTable.Factory());

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
    public void loadDependencyConfigs() {
        lootTableManager.load();
        lootObjectStorage.reload();

        if (hasHotbarSupport()) {
            this.toolbarManager = new ToolbarManager(this);
            this.toolbarManager.load();
        }
    }

    @Override
    public void disable() {

        //        AutomaticPlacer.INST.save();
    }

    @Override
    public void reload() {

        loadConfig();
        getLootTableManager().reload();
        getLootObjectStorage().reload();
    }

    public boolean hasHotbarSupport() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("RCCombatBar");
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TLootTable.class);
        tables.add(TLootTableEntry.class);
        tables.add(TLootTableQuality.class);
        tables.add(TLootTableAlias.class);
        tables.add(TLootObject.class);
        tables.add(TLootPlayer.class);
        return tables;
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
