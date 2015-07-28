package de.raidcraft.loot.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.exceptions.NoLinkedRewardTableException;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.TreasureRewardLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
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
            aliases = {"loot"},
            desc = "Main loot command"
    )
    @NestedCommand(NestedLootCommands.class)
    public void loot(CommandContext context, CommandSender sender) throws CommandException {
        //TODO probably add help
    }

    //    @Command(
    //            aliases = {"lcap"},
    //            desc = "Place automatic chests"
    //    )
    //    @CommandPermissions("autoplace.cmd")
    //    public void autoplace(CommandContext context, CommandSender sender) throws CommandException {
    //
    //        if(context.getString(0).equalsIgnoreCase("resume")) {
    //            if(AutomaticPlacer.INST.config.lastRunning) {
    //                Bukkit.broadcastMessage("Resume placement...");
    //                AutomaticPlacer.INST.resume();
    //            }
    //            else {
    //                Bukkit.broadcastMessage("Nothing to resume!");
    //            }
    //        }
    //
    //        if(context.getString(0).equalsIgnoreCase("start")) {
    //            Player player = (Player)sender;
    //            AutomaticPlacer.INST.run(player.getWorld(), context.getInteger(1));
    //        }
    //
    //        if(context.getString(0).equalsIgnoreCase("delete")) {
    //
    //            int i = 0;
    //            Map<Integer, Map<Integer, List<LootObject>>> lootObjectsCopy = new HashMap<>(LootFactory.INST.getLootObjects());
    //            for(Map.Entry<Block, LootObject> entry : lootObjectsCopy.entrySet()) {
    //                if(entry.getValue().getCreator().contains("AutomaticPlacer")) {
    //                    i++;
    //                    entry.getKey().setType(Material.AIR);
    //                    LootFactory.INST.deleteLootObject(entry.getValue(), false);
    //
    //                    if(i % 100 == 0) {
    //                        Bukkit.broadcastMessage("LCAP removed: " + i);
    //                    }
    //                }
    //            }
    //            Bukkit.broadcastMessage("LCAP removed all ap chests!");
    //        }
    //    }

    @Command(
            aliases = {"autorefill", "infinite"},
            desc = "Creates an infinite dispenser or chest"
    )
    @CommandPermissions("loot.create")
    public void infinite(CommandContext context, CommandSender sender) throws CommandException {

        if (!PlayerListener.createMode.containsKey(sender.getName())) {
            PlayerListener.createMode.put(((Player) sender).getUniqueId(), new SettingStorage(SettingStorage.SETTING_TYPE.TIMED).setCooldown(0).setDrops(SettingStorage.ALL));
        }
        LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
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
                aliases = {"remove", "delete"},
                desc = "Delete a loot objekt"
        )
        @CommandPermissions("loot.delete")
        public void delete(CommandContext context, CommandSender sender) throws CommandException {

            PlayerListener.createMode.put(((Player) sender).getUniqueId(), new SettingStorage(SettingStorage.SETTING_TYPE.REMOVE));
            LootChat.info((Player) sender, "Klicke nun das Loot Objekt an das gelöscht werden soll!");
        }

        @Command(
                aliases = {"editor", "ed"},
                desc = "Toggle editor mode"
        )
        @CommandPermissions("loot.mode.editor")
        public void editorMode(CommandContext context, CommandSender sender) throws CommandException {

            if (PlayerListener.editorMode.contains(sender.getName())) {
                PlayerListener.editorMode.remove(sender.getName());
                LootChat.info((Player) sender, "Du hast den Editor-Modus verlassen!");
            } else {
                PlayerListener.editorMode.add(((Player) sender).getUniqueId());
                LootChat.success((Player) sender, "Du hast den Editor-Modus betreten!");
            }
        }

        @Command(
                aliases = {"admin", "ad"},
                desc = "Toggle admin mode"
        )
        @CommandPermissions("loot.mode.admin")
        public void adminMode(CommandContext context, CommandSender sender) throws CommandException {

            if (PlayerListener.adminMode.contains(sender.getName())) {
                PlayerListener.adminMode.remove(sender.getName());
                LootChat.info((Player) sender, "Du hast den Admin-Modus verlassen!");
            } else {
                PlayerListener.adminMode.add(((Player) sender).getUniqueId());
                LootChat.success((Player) sender, "Du hast den Admin-Modus betreten!");
            }
        }

        @Command(
                aliases = {"timed"},
                min = 1,
                desc = "Creates an timed loot object"
        )
        @CommandPermissions("loot.create")
        public void timed(CommandContext context, CommandSender sender) throws CommandException {

            int cooldown = context.getInteger(0);
            if (cooldown < 0) {
                LootChat.warn((Player) sender, "Der Cooldown muss größer 0 sein!");
                return;
            }
            int drops = SettingStorage.ALL;
            if (context.argsLength() > 1 && context.getInteger(1) > 0) {
                drops = context.getInteger(1);
            }

            PlayerListener.createMode.put(((Player) sender).getUniqueId(), new SettingStorage(SettingStorage.SETTING_TYPE.TIMED).setCooldown(cooldown).setDrops(drops));
            LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
        }

        @Command(
                aliases = {"public"},
                min = 1,
                desc = "Creates an timed loot object"
        )
        @CommandPermissions("loot.public")
        public void publicLoot(CommandContext context, CommandSender sender) throws CommandException {

            int cooldown = context.getInteger(0);
            if (cooldown < 0) {
                LootChat.warn((Player) sender, "Der Cooldown muss größer 0 sein!");
                return;
            }

            PlayerListener.createMode.put(((Player) sender).getUniqueId(), new SettingStorage(SettingStorage.SETTING_TYPE.PUBLIC).setCooldown(cooldown));
            LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
        }

        @Command(
                aliases = {"default"},
                desc = "Creates an default loot object"
        )
        @CommandPermissions("loot.create")
        public void normal(CommandContext context, CommandSender sender) throws CommandException {

            int drops = SettingStorage.ALL;
            if (context.argsLength() > 0 && context.getInteger(0) > 0) {
                drops = context.getInteger(0);
            }

            PlayerListener.createMode.put(((Player) sender).getUniqueId(), new SettingStorage(SettingStorage.SETTING_TYPE.DEFAULT).setDrops(drops));
            LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
        }

        @Command(
                aliases = {"treasure"},
                desc = "Creates an treasure loot object"
        )
        @CommandPermissions("loot.create")
        public void treasure(CommandContext context, CommandSender sender) throws CommandException {

            if (context.argsLength() < 1) {
                LootChat.warn((Player) sender, "Du musst als Parameter eine Belohnungsstufe angeben!");
                return;
            }

            int rewardLevel = context.getInteger(0);
            Player player = (Player) sender;
            try {
                TreasureRewardLevel.getLinkedTable(rewardLevel);
            } catch (NoLinkedRewardTableException e) {
                LootChat.warn(player, "Die angegebene Belohungsstufe existiert nicht!");
                return;
            }

            PlayerListener.createMode.put(player.getUniqueId(), new SettingStorage(SettingStorage.SETTING_TYPE.TREASURE).setRewardLevel(rewardLevel));
            LootChat.info(player, "Klicke nun eine Kiste oder einen Dispenser an!");
        }

        @Command(
                aliases = {"createtable", "create"},
                desc = "Creates a loot table with an alias",
                min = 2,
                usage = "<alias> <minLoot> [maxLoot]"
        )
        @CommandPermissions("loot.create")
        public void createTable(CommandContext args, CommandSender sender) {

            int minLoot = args.getInteger(1);
            PlayerListener.createLootTable.put(((Player) sender).getUniqueId(), new LootTableCreation(args.getString(0), minLoot, args.getInteger(2, minLoot)));
            LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
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
                result = table.getResult();
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
            sender.sendMessage(ChatColor.GREEN + "Looted " + count + "x to get any loot.");
        }
    }
}
