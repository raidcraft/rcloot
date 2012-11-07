package de.raidcraft.de.loot;

import com.sk89q.commandbook.CommandBook;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.Depend;
import com.zachsthings.libcomponents.InjectComponent;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import de.raidcraft.rcrpg.api.database.Database;
import de.raidcraft.rcrpg.loot.commands.LootCommands;
import de.raidcraft.rcrpg.loot.database.tables.LootObjectsTable;
import de.raidcraft.rcrpg.loot.database.tables.LootPlayersTable;
import de.raidcraft.rcrpg.loot.database.tables.LootTableEntriesTable;
import de.raidcraft.rcrpg.loot.database.tables.LootTablesTable;
import de.raidcraft.rcrpg.loot.listener.BlockListener;
import de.raidcraft.rcrpg.loot.listener.PlayerListener;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:51
 * Description:
 */
@ComponentInformation(
        friendlyName = "Loot Module",
        desc = "Provides loot chests and more."
)
@Depend(components = Database.class)
public class LootModule extends BukkitComponent {

    @InjectComponent
    private Database database;
    
    @Override
    public void enable() {

        database.registerTable(LootObjectsTable.class, new LootObjectsTable());
        database.registerTable(LootPlayersTable.class, new LootPlayersTable());
        database.registerTable(LootTableEntriesTable.class, new LootTableEntriesTable());
        database.registerTable(LootTablesTable.class, new LootTablesTable());

        // do some command init
        registerCommands(LootCommands.class);
        // and of course we need some event handlers
        CommandBook.registerEvents(new PlayerListener());
        CommandBook.registerEvents(new BlockListener());

        LootFactory.inst.loadLootObjects(); // loads all existing loot objects from database
    }
}
