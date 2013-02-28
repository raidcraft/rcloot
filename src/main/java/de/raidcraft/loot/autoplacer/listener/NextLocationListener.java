package de.raidcraft.loot.autoplacer.listener;

import de.raidcraft.loot.autoplacer.events.NextLocationFoundEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Philip
 */
public class NextLocationListener implements Listener {

    @EventHandler
    public void onNextLocation(NextLocationFoundEvent event) {

        event.getNextLocation().getWorld().getBlockAt(event.getNextLocation()).setType(Material.GLOWSTONE);
    }
}
