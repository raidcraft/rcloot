package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
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

import java.util.Collection;
import java.util.HashMap;
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
        RDSTable lootTable = lootObject.getLootTable();
        int count = lootTable.getCount();
        lootTable.setCount(1);
        Collection<RDSObject> loot = lootObject.loot(LootFactory.ANY);
        lootTable.setCount(count);
        if (loot.isEmpty()) {
            event.setCancelled(true);
            return;
        }

        Inventory inventory;
        if (event.getBlock().getState() instanceof Dispenser) {
            inventory = ((Dispenser) event.getBlock().getState()).getInventory();
        } else {
            inventory = ((Dropper) event.getBlock().getState()).getInventory();
        }

        for (RDSObject rdsObject : loot) {
            if (rdsObject instanceof Dropable) {
                event.setItem(((Dropable) rdsObject).getItemStack());
                inventory.addItem(((Dropable) rdsObject).getItemStack())
                break;
            }
        }
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
