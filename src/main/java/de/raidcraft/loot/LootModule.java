package de.raidcraft.loot;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;
import de.raidcraft.loot.commands.LootCommands;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import de.raidcraft.loot.database.tables.LootTableEntriesTable;
import de.raidcraft.loot.database.tables.LootTablesTable;
import de.raidcraft.loot.listener.BlockListener;
import de.raidcraft.loot.listener.PlayerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:51
 * Description:
 */
@ComponentInformation(
        friendlyName = "Loot Module",
        desc = "Provides loot chests and more."
)
public class LootModule extends BukkitComponent {

    public LocalConfiguration config;

    @Override
    public void enable() {
        config = configure(new LocalConfiguration());
        ComponentDatabase.INSTANCE.registerTable(LootObjectsTable.class, new LootObjectsTable());
        ComponentDatabase.INSTANCE.registerTable(LootPlayersTable.class, new LootPlayersTable());
        ComponentDatabase.INSTANCE.registerTable(LootTableEntriesTable.class, new LootTableEntriesTable());
        ComponentDatabase.INSTANCE.registerTable(LootTablesTable.class, new LootTablesTable());

        // do some command init
        registerCommands(LootCommands.class);
        // and of course we need some event handlers
        CommandBook.registerEvents(new PlayerListener());
        CommandBook.registerEvents(new BlockListener());

        TreasureRewardLevel.addRewardLevel(1, config.rewardLevel1);
        TreasureRewardLevel.addRewardLevel(2, config.rewardLevel2);
        TreasureRewardLevel.addRewardLevel(3, config.rewardLevel3);
        TreasureRewardLevel.addRewardLevel(4, config.rewardLevel4);
        TreasureRewardLevel.addRewardLevel(5, config.rewardLevel5);
        
        LootFactory.inst.loadLootObjects(); // loads all existing loot objects from database
    }

    public class LocalConfiguration extends ConfigurationBase {
        @Setting("reward-level-table-1") public int rewardLevel1 = 0;
        @Setting("reward-level-table-2") public int rewardLevel2 = 0;
        @Setting("reward-level-table-3") public int rewardLevel3 = 0;
        @Setting("reward-level-table-4") public int rewardLevel4 = 0;
        @Setting("reward-level-table-5") public int rewardLevel5 = 0;
    }
}
