package de.raidcraft.loot.autoplacer;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.autoplacer.events.NextLocationFoundEvent;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Philip
 */
public class SpiralChecker {

    private World world;
    private int maxCoordinate;
    private int pointDistance;

    public SpiralChecker(World world, int maxCoordinate, int pointDistance) {

        this.world = world;
        this.maxCoordinate = maxCoordinate;
        this.pointDistance = pointDistance;
    }

    public void doRecursiveSpiral(int ring) {

        // max radius reached
        if(ring > maxCoordinate / pointDistance) {
            return;
        }

        int i;
        Location location = new Location(world, -ring*pointDistance, 3, -ring*pointDistance);

        RaidCraft.callEvent(new NextLocationFoundEvent(location));

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setZ(location.getBlockZ() + pointDistance);
            RaidCraft.callEvent(new NextLocationFoundEvent(location));
        }

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setX(location.getBlockX() + pointDistance);
            RaidCraft.callEvent(new NextLocationFoundEvent(location));
        }

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setZ(location.getBlockZ() - pointDistance);
            RaidCraft.callEvent(new NextLocationFoundEvent(location));
        }

        for(i = 1; i < (ring * 2); i++) {
            location.setX(location.getBlockX() - pointDistance);
            RaidCraft.callEvent(new NextLocationFoundEvent(location));
        }

        // recursive call
        doRecursiveSpiral(ring + 1);
    }
}
