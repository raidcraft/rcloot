package de.raidcraft.loot;

import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.HotbarManager;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.loot.hotbar.LootAdminToolbar;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class ToolbarManager {

    private final LootPlugin plugin;
    private final HotbarManager hotbarManager = RaidCraft.getComponent(HotbarManager.class);

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
