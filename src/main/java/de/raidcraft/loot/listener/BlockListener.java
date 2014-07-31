package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:26
 * Description:
 */
public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage().getLootObject(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
            LootChat.warn(event.getPlayer(), "Loot Objekte können nicht zerstört werden!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {

        Map<Block, Material> lootBlocks = new HashMap<>();
        for (Block block : event.blockList()) {
            if (RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage().getLootObject(block.getLocation()) != null) {
                lootBlocks.put(block, block.getType());
            }
        }
        if (lootBlocks.size() > 0) {
            TNTPlacerTask task = new TNTPlacerTask(lootBlocks);
            Bukkit.getScheduler().scheduleSyncDelayedTask(RaidCraft.getComponent(LootPlugin.class), task, 20 * 5);
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {

        if (RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage().getLootObject(event.getBlock().getLocation()) == null) {
            return;
        }

        LootObject lootObject = RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage().getLootObject(event.getBlock().getLocation());
        List<ItemStack> loot = lootObject.loot(LootFactory.ANY);
        if (loot.size() == 0) loot.add(new ItemStack(Material.DIRT, 1));    // force add item if database error occurred

        Inventory inventory;
        if (event.getBlock().getState() instanceof Dispenser) {
            inventory = ((Dispenser) event.getBlock().getState()).getInventory();
        } else {
            inventory = ((Dropper) event.getBlock().getState()).getInventory();
        }

        loot.stream().forEach(l -> {
            if (event.getItem().getType().name().equals(l.getType().name())) {
                inventory.addItem(event.getItem());
            }
        });
    }

    public class TNTPlacerTask implements Runnable {

        Map<Block, Material> chestBlocks;

        public TNTPlacerTask(Map<Block, Material> chestBlocks) {

            this.chestBlocks = chestBlocks;
        }

        @Override
        public void run() {

            for (Map.Entry<Block, Material> entry : chestBlocks.entrySet()) {
                entry.getKey().setType(entry.getValue());
            }
        }
    }
}
