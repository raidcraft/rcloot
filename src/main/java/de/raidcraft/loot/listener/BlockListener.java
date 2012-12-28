package de.raidcraft.loot.listener;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.loot.LootFactory;
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(CommandBook.inst(), task, 20*5);
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {

        if (LootFactory.inst.getLootObject(event.getBlock()) == null) {
            return;
        }

        LootObject lootObject = LootFactory.inst.getLootObject(event.getBlock());
        List<ItemStack> loot = lootObject.loot(LootFactory.ANY);
        Dispenser dispenser = (Dispenser) event.getBlock().getState();

        dispenser.getInventory().clear();
        dispenser.getInventory().setContents(loot.toArray(new ItemStack[loot.size()]));
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
