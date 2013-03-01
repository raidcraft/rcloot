package de.raidcraft.loot.autoplacer;

import org.bukkit.Location;

/**
 * @author Philip
 */
public class CreatedChestInformation {

    private Location location;

    public CreatedChestInformation(Location location) {

        this.location = location;
    }

    public Location getLocation() {

        return location;
    }
}
