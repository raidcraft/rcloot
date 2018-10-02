package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.object.LootObjectStorage;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.commands.LootTableCreation;
import de.raidcraft.loot.loothost.LootHost;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:25
 * Description:
 */
public class PlayerListener implements Listener {

    public static Map<UUID, SettingStorage> createMode = new HashMap<>();
    public static Map<UUID, LootTableCreation> createLootTable = new HashMap<>();
    public static List<UUID> editorMode = new ArrayList<>();
    public static List<UUID> adminMode = new ArrayList<>();
    private Map<UUID, LootObject> inventoryLocks = new HashMap<>();

    /*
     * Prevent inventory open if inventory holder is loot object
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        LootHost lootHost = RaidCraft.getComponent(LootPlugin.class).getLootHostManager().getLootHost(event.getInventory());
        if (lootHost == null) return;

        Block block = lootHost.getBlock(event.getInventory());
        if (block == null) return;

        LootObjectStorage lootObjectStorage = RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage();
        LootObject lootObject = lootObjectStorage.getLootObject(block.getLocation());
        if (lootObject == null) {
            return;
        }

        HumanEntity entity = event.getPlayer();

        if (!(entity instanceof Player)) {
            return;
        }

        if (entity.getGameMode() == GameMode.SPECTATOR || entity.getGameMode() == GameMode.CREATIVE) {
            if (!adminMode.contains(entity.getUniqueId())) {
                LootChat.warn((Player) entity, "Du kannst in diesem Gamemode keine Lootkisten öffnen.");
                event.setCancelled(true);
                return;
            }
        }

        if (!lootHost.canBeOpened() && !entity.hasPermission("loot.admin")) {
            LootChat.warn((Player) entity, "Du hast keine Rechte um Loot-Dispenser zu öffnen!");
            event.setCancelled(true);
            return;
        }

        List<ItemStack> loot;

        // check if locked
        if (inventoryLocks.containsValue(lootObject)) {
            LootChat.warn((Player) entity, "Du musst kurz warten bis ein anderer Spieler fertig gelootet hat!");
            event.setCancelled(true);
            return;
        }

        // fill public loot chest if cooldown over
        if (lootObject.isPublicLootObject()) {
            loot = lootObject.loot(entity.getUniqueId());
            if (loot.size() > 0) {
                event.getInventory().setContents(loot.toArray(new ItemStack[0]));
            }
            return;
        }

        // lock loot object
        inventoryLocks.put(entity.getUniqueId(), lootObject);
        loot = lootObject.loot(entity.getUniqueId());
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
            if (!adminMode.contains(event.getPlayer().getUniqueId())) {

                if (!lootObject.isInfinite() || lootObject.getCooldown() > 0) {
                    for (ItemStack itemStack : event.getInventory().getContents()) {
                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                            event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
                        }
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
    public void onPlayerQuiet(PlayerQuitEvent event) {

        if (inventoryLocks.containsKey(event.getPlayer().getUniqueId())) {
            inventoryLocks.remove(event.getPlayer().getUniqueId());
        }
        editorMode.remove(event.getPlayer().getUniqueId());
        adminMode.remove(event.getPlayer().getUniqueId());
        createMode.remove(event.getPlayer().getUniqueId());
    }
}
