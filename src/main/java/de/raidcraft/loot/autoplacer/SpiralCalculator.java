package de.raidcraft.loot.autoplacer;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Stack;

/**
 * @author Philip
 */
public class SpiralCalculator {

    private World world;
    private int maxCoordinate;
    private int pointDistance;

    private Stack<Location> locations;

    public SpiralCalculator(World world, int maxCoordinate, int pointDistance) {

        this.world = world;
        this.maxCoordinate = maxCoordinate;
        this.pointDistance = pointDistance;
        this.locations = new Stack<>();
    }

    public void doSpiral(int ring) {

        // max radius reached
        if(ring > maxCoordinate / pointDistance) {
            return;
        }

        int i;
        Location location = new Location(world, -ring*pointDistance, 3, -ring*pointDistance);

        locations.push(location.clone());

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setZ(location.getBlockZ() + pointDistance);
            locations.push(location.clone());
        }

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setX(location.getBlockX() + pointDistance);
            locations.push(location.clone());
        }

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setZ(location.getBlockZ() - pointDistance);
            locations.push(location.clone());
        }

        for(i = 1; i < (ring * 2); i++) {
            location.setX(location.getBlockX() - pointDistance);
            locations.push(location.clone());
        }

        // recursive call
        doSpiral(ring + 1);
    }

    public Stack<Location> getLocations() {

        return locations;
    }
}
