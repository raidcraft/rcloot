package de.raidcraft.loot.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.ConfirmCommand;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
                aliases = {"admin", "ad"},
                desc = "Toggle admin mode"
        )
        @CommandPermissions("loot.mode.admin")
        public void adminMode(CommandContext context, CommandSender sender) throws CommandException {

            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;

            if (plugin.hasHotbarSupport()) {
                plugin.getToolbarManager().toggleLootToolbar(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Hotbar Support nicht aktiviert. Bitte installiere das RCCombatBar Plugin.");
            }
        }

        @Command(
                aliases = {"respawn"},
                desc = "Respawns all destroyable loot-objects"
        )
        @CommandPermissions("loot.respawn")
        public void respawn(CommandContext context, CommandSender sender) throws CommandException {

            long count = plugin.getLootObjectManager().respawnDestroyedLootObjects(true);
            sender.sendMessage(ChatColor.GREEN + "Es wurden " + count + " Loot-Objekte respawned.");
        }

        @Command(
                aliases = {"simulate"},
                desc = "Simulates the looting of the given loot table",
                usage = "<table> <level>",
                min = 1
        )
        @CommandPermissions("loot.simulate")
        public void simulate(CommandContext args, CommandSender sender) throws CommandException {

            RDSTable table = plugin.getLootTableManager().getLevelDependantLootTable(args.getString(0), args.getInteger(1, 1));
            if (table == null) {
                throw new CommandException("The loot table " + args.getString(0) + " does not exist!");
            }
            int count = 0;
            Collection<RDSObject> result = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                result = table.loot((Player) sender);
                if (!result.isEmpty()) {
                    count = i;
                    break;
                }
            }

            if (result.isEmpty()) {
                throw new CommandException("Could not get a valid result after 1000 iterations! Is the loot table configured correctly?");
            }

            Inventory inventory = Bukkit.createInventory((Player) sender, 54);
            result.stream().filter(rdsObject -> rdsObject instanceof Dropable).forEach(rdsObject -> {
                ItemStack itemStack = ((Dropable) rdsObject).getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = itemMeta.getLore();
                lore.add("Chance: " + rdsObject.getProbability());
                lore.add("Source: " + rdsObject.getTable());
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                inventory.addItem(itemStack);
            });
            ((Player) sender).openInventory(inventory);
            sender.sendMessage(ChatColor.GREEN + "Looted " + (count + 1) + "x to get any loot.");
        }
    }
}
