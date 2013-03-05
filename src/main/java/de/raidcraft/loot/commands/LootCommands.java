package de.raidcraft.loot.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.autoplacer.AutomaticPlacer;
import de.raidcraft.loot.exceptions.NoLinkedRewardTableException;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.TreasureRewardLevel;
import org.bukkit.Bukkit;
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
            aliases = {"loot"},
            desc = "Main loot command"
    )
    @NestedCommand(NestedLootCommands.class)
    public void loot(CommandContext context, CommandSender sender) throws CommandException {
        //TODO probably add help
    }

    @Command(
            aliases = {"lcap"},
            desc = "Place automatic chests"
    )
    @CommandPermissions("autoplace.cmd")
    public void autoplace(CommandContext context, CommandSender sender) throws CommandException {

        if(context.getString(0).equalsIgnoreCase("resume")) {
            if(AutomaticPlacer.INST.config.lastRunning) {
                Bukkit.broadcastMessage("Resume placement...");
                AutomaticPlacer.INST.resume();
            }
            else {
                Bukkit.broadcastMessage("Nothing to resume!");
            }
        }

        if(context.getString(0).equalsIgnoreCase("start")) {
            Player player = (Player)sender;
            AutomaticPlacer.INST.run(player.getWorld(), context.getInteger(1));
        }

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
    }

    @Command(
            aliases = {"autorefill", "infinite"},
            desc = "Creates an infinite dispenser or chest"
    )
    @CommandPermissions("loot.create")
    public void infinite(CommandContext context, CommandSender sender) throws CommandException {

        if (!PlayerListener.createMode.containsKey(sender.getName())) {
            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.TIMED).setCooldown(0).setDrops(SettingStorage.ALL));
        }
        LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
    }

    public static class NestedLootCommands {

        private final LootPlugin module;

        public NestedLootCommands(LootPlugin module) {

            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads config and shit"
        )
        @CommandPermissions("loot.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            RaidCraft.getComponent(LootPlugin.class).reload();
            AutomaticPlacer.INST.config.reload();
            LootChat.info((Player) sender, "Das Loot-Plugin wurde neugeladen!");
        }

        @Command(
                aliases = {"remove", "delete"},
                desc = "Delete a loot objekt"
        )
        @CommandPermissions("loot.delete")
        public void delete(CommandContext context, CommandSender sender) throws CommandException {

            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.REMOVE));
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
                PlayerListener.editorMode.add(sender.getName());
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
                PlayerListener.adminMode.add(sender.getName());
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

            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.TIMED).setCooldown(cooldown).setDrops(drops));
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

            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.PUBLIC).setCooldown(cooldown));
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

            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.DEFAULT).setDrops(drops));
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

            try {
                TreasureRewardLevel.getLinkedTable(rewardLevel);
            } catch (NoLinkedRewardTableException e) {
                LootChat.warn((Player) sender, "Die angegebene Belohungsstufe existiert nicht!");
                return;
            }

            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.TREASURE).setRewardLevel(rewardLevel));
            LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
        }
    }
}
