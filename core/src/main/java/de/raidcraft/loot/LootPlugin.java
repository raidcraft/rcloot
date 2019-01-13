package de.raidcraft.loot;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.commands.LootCommands;
import de.raidcraft.loot.listener.BlockListener;
import de.raidcraft.loot.listener.InventoryListener;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.tables.TLootPlayer;
import lombok.Getter;

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
    private LootObjectManager lootObjectManager;
    @Getter
    private LootTableManager lootTableManager;

    @Override
    public void enable() {

        config = configure(new LocalConfiguration(this));

        registerCommands(LootCommands.class);
        registerEvents(new PlayerListener(this));
        registerEvents(new BlockListener(this));
        registerEvents(new InventoryListener(this));

        lootObjectManager = new LootObjectManager(this);
        lootFactory = new LootFactory(this);
        lootTableManager = new LootTableManager(this);
    }

    @Override
    public void loadDependencyConfigs() {
        lootTableManager.load();
        lootObjectManager.reload();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

        getLootTableManager().reload();
        this.getLootObjectManager().reload();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TLootObject.class);
        tables.add(TLootPlayer.class);
        return tables;
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("conversations.create-loot-object")
        public String createLootObjectConversation = "plugins.create-loot-object";
        @Setting("respawn-interval")
        @Comment("Interval of respawn task for loot-objects in ticks. 100 = 5s")
        public long respawnIntervalInTicks = 100;
        @Setting("simulation-command-count")
        @Comment("Defines how often a loot table should be looted if nothing drops.")
        public int lootTableSimulationCount = 10;

        public LocalConfiguration(LootPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
