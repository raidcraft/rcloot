package de.raidcraft.loot.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootModule;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.TreasureRewardLevel;
import de.raidcraft.loot.exceptions.NoLinkedRewardTableException;
import de.raidcraft.loot.listener.PlayerListener;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 16.10.12 - 19:46
 * Description:
 */
public class LootCommands {

    public LootCommands(LootModule module) {

    }

    @Command(
            aliases = {"loot"},
            desc = "Main loot command"
    )
    @NestedCommand(NestedLootCommands.class)
    public void loot(CommandContext context, CommandSender sender) throws CommandException {
        //TODO add help
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

        private final LootModule module;

        public NestedLootCommands(LootModule module) {

            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads the loot module"
        )
        @CommandPermissions("loot.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {
            module.reload();
            module.loadConfig();
            LootFactory.inst.loadLootObjects();
            LootChat.success((Player) sender, "Das Loot-Plugin wurde neugeladen!");
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

            if(context.argsLength() < 1) {
                LootChat.warn((Player)sender, "Du musst als Parameter eine Belohnungsstufe angeben!");
                return;
            }

            int drops = SettingStorage.ALL;
            int rewardLevel = context.getInteger(0);

            try {
                TreasureRewardLevel.getLinkedTable(rewardLevel);
            }
            catch(NoLinkedRewardTableException e) {
                LootChat.warn((Player) sender, "Die angegebene Belohungsstufe existiert nicht!");
                return;
            }

            if (context.argsLength() > 1 && context.getInteger(1) > 0) {
                drops = context.getInteger(1);
            }

            PlayerListener.createMode.put(sender.getName(), new SettingStorage(SettingStorage.SETTING_TYPE.TREASURE).setDrops(drops).setRewardLevel(rewardLevel));
            LootChat.info((Player) sender, "Klicke nun eine Kiste oder einen Dispenser an!");
        }
    }
}
