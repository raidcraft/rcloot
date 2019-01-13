package de.faldoria.loot.toolbar;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.combatbar.HotbarManager;
import de.raidcraft.combatbar.api.Hotbar;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class ToolbarManager {

    private final BasePlugin plugin;
    private final HotbarManager hotbarManager;

    public void load() {
        getHotbarManager().registerHotbarType(getPlugin(), LootAdminToolbar.class);
    }

    public void toggleLootToolbar(Player player) {
        Hotbar hotbar = getHotbarManager().getOrCreateHotbar(player, LootAdminToolbar.class, false);
        if (hotbar.isActive()) {
            hotbar.getHolder().removeHotbar(hotbar);
        } else {
            hotbar.getHolder().activate(hotbar);
        }
    }
}
