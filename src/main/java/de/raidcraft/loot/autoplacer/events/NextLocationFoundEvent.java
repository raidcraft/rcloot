package de.raidcraft.loot.autoplacer.events;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Philip
 */
public class NextLocationFoundEvent extends Event {

    private Location nextLocation;

    public NextLocationFoundEvent(Location nextLocation) {

        this.nextLocation = nextLocation;
    }

    public Location getNextLocation() {

        return nextLocation;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
