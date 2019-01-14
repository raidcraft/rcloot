package de.faldoria.loot.toolbar;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LootAdminToolbarCommands {

    private final AdminToolbarComponent component;

    public LootAdminToolbarCommands(LootPlugin plugin) {
        this.component = RaidCraft.getComponent(AdminToolbarComponent.class);
    }

    @Command(
            aliases = {"loottoolbar", "lootadmintoolbar", "lat"},
            desc = "Toggles the loot admin toolbar on and off."
    )
    @CommandPermissions("loot.mode.admin")
    public void toggleToolbar(CommandContext args, CommandSender sender) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if (component.hasHotbarSupport()) {
            component.getToolbarManager().toggleLootToolbar(player);
        } else {
            sender.sendMessage(ChatColor.RED + "Hotbar Support nicht aktiviert. Bitte installiere das RCCombatBar Plugin.");
        }
    }
}
