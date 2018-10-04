package de.raidcraft.loot.listener;

import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.lootobjects.LootObject;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.skills.util.ItemUtil;
import de.raidcraft.util.BlockUtil;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.ItemUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:25
 * Description:
 */
@Data
public class PlayerListener implements Listener {

    private final Map<UUID, LootObject> inventoryLocks = new HashMap<>();
    private final LootPlugin plugin;

    /*
     * Prevent inventory open if inventory holder is loot object
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        if (!(event.getPlayer() instanceof Player)) return;

        Optional<LootObject> optionalLootObject = getPlugin().getLootObjectManager().getLootObject(event.getInventory());

        if (!optionalLootObject.isPresent()) return;

        Player player = (Player) event.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
            LootChat.warn(player, "Du kannst in diesem Gamemode keine Lootkisten öffnen.");
            event.setCancelled(true);
            return;
        }

        List<ItemStack> loot;

        LootObject lootObject = optionalLootObject.get();

        // check if locked
        if (inventoryLocks.containsValue(lootObject)) {
            LootChat.warn(player, "Du musst kurz warten bis ein anderer Spieler fertig gelootet hat!");
            event.setCancelled(true);
            return;
        }

        // fill public loot chest if cooldown over
        if (lootObject.isPublicLootObject()) {
            loot = lootObject.loot(player.getUniqueId());
            if (loot.size() > 0) {
                event.getInventory().setContents(loot.toArray(new ItemStack[0]));
            }
            return;
        }

        // lock loot object
        inventoryLocks.put(player.getUniqueId(), lootObject);
        loot = lootObject.loot(player.getUniqueId());
        // set loot
        event.getInventory().clear();

        // cut loot if too many items
        if (loot.size() > event.getInventory().getSize()) {
            loot = loot.subList(0, event.getInventory().getSize());
        }

        event.getInventory().setContents(loot.toArray(new ItemStack[0]));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (inventoryLocks.containsKey(event.getPlayer().getUniqueId())) {
            LootObject lootObject = inventoryLocks.get(event.getPlayer().getUniqueId());
            inventoryLocks.remove(event.getPlayer().getUniqueId());

            // drop not cleared items if loot object isn't infinite
            if (!lootObject.isInfinite() || lootObject.getCooldown() > 0) {
                for (ItemStack itemStack : event.getInventory().getContents()) {
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        event.getPlayer().getLocation().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemStack);
                    }
                }
            }

            // fill dispenser otherwise the dispenser event won't be called
            if (event.getInventory().getType() == InventoryType.DISPENSER) {
                List<ItemStack> loot = lootObject.loot(LootFactory.ANY);
                event.getInventory().clear();
                if (loot.size() == 0) loot.add(new ItemStack(Material.STONE, 1));    // force add item if database error occurred
                for (ItemStack item : loot) {
                    // create item stack
                    ItemStack newItemStack = item.clone();
                    event.getInventory().addItem(newItemStack);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // let the methods above handle all inventory interactions
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getState() instanceof Container) return;

        getPlugin().getLootObjectManager().getLootObject(event.getClickedBlock()).ifPresent(object -> {
            event.setCancelled(true);

            if (object.isDestroyable()) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) return;
                object.destroy();
            } else {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;
                String name = CustomItemUtil.encodeItemId((int) object.getId()) + ChatColor.GOLD + ItemUtils.getFriendlyName(event.getClickedBlock().getType());
                Inventory inventory = Bukkit.createInventory(event.getPlayer(), InventoryType.CHEST, name);
                inventory.setContents(object.loot(event.getPlayer().getUniqueId()).toArray(new ItemStack[0]));
                event.getPlayer().openInventory(inventory);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        inventoryLocks.remove(event.getPlayer().getUniqueId());
    }
}
