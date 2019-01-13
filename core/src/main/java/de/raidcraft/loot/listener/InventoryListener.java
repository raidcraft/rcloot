package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author mdoering
 */
public class InventoryListener implements Listener {

    private final LootPlugin plugin;

    public InventoryListener(LootPlugin plugin) {

        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        plugin.getLootObjectManager().getLootObject(event.getClickedInventory()).ifPresent(object -> {
            if (!object.isEnabled()) return;
            ItemStack item = event.getClickedInventory().getItem(event.getSlot());
            if (RaidCraft.isQuestItem(item)) {
                Quests.getQuestProvider().ifPresent(questProvider -> {
                    event.getClickedInventory().clear(event.getSlot());
                    questProvider.addQuestItem((Player) event.getWhoClicked(), item);
                });
            }
        });
    }
}
