package de.faldoria.loot.toolbar;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.components.BukkitComponent;
import de.raidcraft.api.components.ComponentInformation;
import de.raidcraft.api.components.Depend;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.combatbar.HotbarManager;
import de.raidcraft.loot.LootPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@ComponentInformation(
        value = "admin-toolbar",
        friendlyName = "Loot Admin Toolbar",
        desc = "An admin toolbar that depends on the RCCombatBar plugin to manage loot objects.",
        authors = "Silthus"
)
@Depend(plugins = {"RCCombatBar"})
@Getter
public class AdminToolbarComponent extends BukkitComponent {

    private ToolbarManager toolbarManager;

    @Override
    public void enable() {
        if (hasHotbarSupport()) {
            Conversations.registerConversationType("create-loot-object", CreateLootObjectConversation.class);
            this.toolbarManager = new ToolbarManager(getPlugin(), RaidCraft.getComponent(HotbarManager.class));
            this.toolbarManager.load();
            registerCommands(LootAdminCommand.class);
        }
    }

    private boolean hasHotbarSupport() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("RCCombatBar");
        return plugin != null && plugin.isEnabled();
    }

    public class LootAdminCommand {

        private final LootPlugin plugin;

        public LootAdminCommand(LootPlugin plugin) {
            this.plugin = plugin;
        }

        @Command(
                aliases = {"loottoolbar", "lootadmintoolbar", "lat"},
                desc = "Toggles the loot admin toolbar on and off."
        )
        @CommandPermissions("loot.mode.admin")
        public void toggleToolbar(CommandSender sender, CommandContext args) {
            if (!(sender instanceof Player)) return;
            Player player = (Player) sender;

            if (hasHotbarSupport()) {
                getToolbarManager().toggleLootToolbar(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Hotbar Support nicht aktiviert. Bitte installiere das RCCombatBar Plugin.");
            }
        }
    }
}
