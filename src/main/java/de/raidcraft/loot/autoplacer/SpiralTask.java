package de.raidcraft.loot.autoplacer;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Philip
 */
public class SpiralTask implements Runnable {

    private World world;
    private int maxCoordinate;
    private int pointDistance;

    public SpiralTask(World world, int maxCoordinate, int pointDistance) {

        this.world = world;
        this.maxCoordinate = maxCoordinate;
        this.pointDistance = pointDistance;
    }

    @Override
    public void run() {
        doSpiral(0);
    }

    public void doSpiral(int ring) {

        // max radius reached
        if(ring > maxCoordinate / pointDistance) {
            return;
        }

        int i;
        Location location = new Location(world, -ring*pointDistance, 3, -ring*pointDistance);

        LocationChecker.INST.checkNextLocation(location.clone());

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setZ(location.getBlockZ() + pointDistance);
            LocationChecker.INST.checkNextLocation(location.clone());
        }

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setX(location.getBlockX() + pointDistance);
            LocationChecker.INST.checkNextLocation(location.clone());
        }

        for(i = 1; i < ((ring * 2) + 1); i++) {
            location.setZ(location.getBlockZ() - pointDistance);
            LocationChecker.INST.checkNextLocation(location.clone());
        }

        for(i = 1; i < (ring * 2); i++) {
            location.setX(location.getBlockX() - pointDistance);
            LocationChecker.INST.checkNextLocation(location.clone());
        }

        // recursive call
        doSpiral(ring + 1);
    }
}
