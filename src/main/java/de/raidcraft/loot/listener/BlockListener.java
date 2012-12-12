package de.raidcraft.loot.listener;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

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

        if (LootFactory.inst.getLootObject(event.getBlock()) != null) {
            event.setCancelled(true);
            LootChat.warn(event.getPlayer(), "Loot Objekte können nicht zerstört werden!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        List<Block> lootBlocks = new ArrayList<>();
        for(Block block : event.blockList()) {
            if (LootFactory.inst.getLootObject(block) != null) {
                lootBlocks.add(block);
            }
        }
        if(lootBlocks.size() > 0) {
            TNTPlacerTask task = new TNTPlacerTask(lootBlocks);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)CommandBook.inst(), task, 20*5);
        }
    }
    
    public class TNTPlacerTask implements Runnable {
        List<Block> chestBlocks;
        
        public TNTPlacerTask(List<Block> chestBlocks) {
            this.chestBlocks = chestBlocks;
        }
        @Override
        public void run() {
            for(Block block : chestBlocks) {
                block.setType(Material.CHEST);
            }
        }
    }
}
