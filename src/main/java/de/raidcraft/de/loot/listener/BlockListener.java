package de.raidcraft.de.loot.listener;

import de.raidcraft.rcrpg.loot.LootFactory;
import de.raidcraft.rcrpg.loot.util.LootChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:26
 * Description:
 */
public class BlockListener implements Listener {

    @EventHandler( ignoreCancelled = true )
    public void onBlockBreak(BlockBreakEvent event) {
        if(LootFactory.inst.getLootObject(event.getBlock()) != null) {
            event.setCancelled(true);
            LootChat.warn(event.getPlayer(), "Loot Objekte können nicht zerstört werden!");
        }
    }
}
