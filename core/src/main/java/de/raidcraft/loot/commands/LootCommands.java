package de.raidcraft.loot.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 16.10.12 - 19:46
 * Description:
 */
public class LootCommands {

    public LootCommands(LootPlugin module) {

    }

    @Command(
            aliases = {"loot", "rcloot"},
            desc = "Main loot command"
    )
    @NestedCommand(NestedLootCommands.class)
    public void loot(CommandContext context, CommandSender sender) throws CommandException {
        //TODO probably add help
    }

    public static class NestedLootCommands {

        private final LootPlugin plugin;

        public NestedLootCommands(LootPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads config and shit"
        )
        @CommandPermissions("loot.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            RaidCraft.getComponent(LootPlugin.class).reload();
            //            AutomaticPlacer.INST.config.reload();
            LootChat.info((Player) sender, "Das Loot-Plugin wurde neugeladen!");
        }

        @Command(
                aliases = {"respawn"},
                desc = "Respawns all destroyable loot-objects"
        )
        @CommandPermissions("loot.respawn")
        public void respawn(CommandContext context, CommandSender sender) throws CommandException {

            long allObjects = plugin.getRcDatabase().find(TLootObject.class)
                    .where().eq("destroyable", true)
                    .and().isNotNull("destroyed")
                    .findList()
                    .stream().map(tLootObject -> plugin.getLootFactory().createLootObject(tLootObject))
                    .count();
            long count = plugin.getLootObjectManager().respawnDestroyedLootObjects(true);
            sender.sendMessage(ChatColor.GREEN + "Es wurden " + count + "/" + allObjects + " Loot-Objekte respawned.");
        }
    }
}
