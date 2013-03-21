package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:26
 * Description:
 */
public class BlockListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (LootFactory.INST.getLootObject(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
            LootChat.warn(event.getPlayer(), "Loot Objekte können nicht zerstört werden!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {

        List<Block> lootBlocks = new ArrayList<>();
        for (Block block : event.blockList()) {
            if (LootFactory.INST.getLootObject(block.getLocation()) != null) {
                lootBlocks.add(block);
            }
        }
        if (lootBlocks.size() > 0) {
            TNTPlacerTask task = new TNTPlacerTask(lootBlocks);
            Bukkit.getScheduler().scheduleSyncDelayedTask(RaidCraft.getComponent(LootPlugin.class), task, 20 * 5);
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {

        if (LootFactory.INST.getLootObject(event.getBlock().getLocation()) == null) {
            return;
        }

        LootObject lootObject = LootFactory.INST.getLootObject(event.getBlock().getLocation());
        List<ItemStack> loot = lootObject.loot(LootFactory.ANY);
        if (loot.size() == 0) loot.add(new ItemStack(Material.STONE, 1));    // force add item if database error occurred

        Dispenser dispenser = (Dispenser) event.getBlock().getState();

        dispenser.getInventory().setContents(loot.toArray(new ItemStack[loot.size()]));
    }

    public class TNTPlacerTask implements Runnable {

        List<Block> chestBlocks;

        public TNTPlacerTask(List<Block> chestBlocks) {

            this.chestBlocks = chestBlocks;
        }

        @Override
        public void run() {

            for (Block block : chestBlocks) {
                block.setType(Material.CHEST);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        LootFactory.INST.loadObjects(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        LootFactory.INST.unloadObjects(event.getChunk());
    }
}
