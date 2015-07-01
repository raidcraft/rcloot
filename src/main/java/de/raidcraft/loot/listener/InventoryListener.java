package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.api.quests.QuestProvider;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.loothost.LootHost;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

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
        LootHost lootHost = plugin.getLootHostManager().getLootHost(event.getClickedInventory());
        if (lootHost == null) return;
        Block block = lootHost.getBlock(event.getClickedInventory());
        if (block == null) return;
        LootObject lootObject = plugin.getLootObjectStorage().getLootObject(block.getLocation());
        if (lootObject == null) return;
        if (!lootObject.isEnabled()) return;
        ItemStack item = event.getClickedInventory().getItem(event.getSlot());
        if (item != null && RaidCraft.isCustomItem(item)) {
            CustomItemStack customItem = RaidCraft.getCustomItem(item);
            if (customItem.getItem().getType() == ItemType.QUEST) {
                Optional<QuestProvider> questProvider = Quests.getQuestProvider();
                if (questProvider.isPresent()) {
                    event.getClickedInventory().setItem(event.getSlot(), null);
                    questProvider.get().addQuestItem((Player) event.getWhoClicked(), item);
                }
            }
        }
    }
}
