package de.raidcraft.loot.autoplacer;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Philip
 */
public class AutomaticPlacer implements Component {

    public static AutomaticPlacer INST = null;

    public final static int MAX_ENQUEUD = 100;
    public static List<Material> badGroundMaterials = new ArrayList<>();

    public int totalLocations = 0;
    public int checkedLocations = 0;
    public int checkerTaskId = 0;

    public LocalConfiguration config = new LocalConfiguration(LootPlugin.INST);

    {
        badGroundMaterials.add(Material.WATER);
        badGroundMaterials.add(Material.FENCE);
        badGroundMaterials.add(Material.YELLOW_FLOWER);
        badGroundMaterials.add(Material.RED_ROSE);
        badGroundMaterials.add(Material.GRASS);
    }

    public AutomaticPlacer() {
        INST = this;
        config.load(true);
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("min-distance-surface")
        public int surfaceMinDistance = 100;
        @Setting("max-distance-surface")
        public int surfaceMaxDistance = 500;
        @Setting("min-distance-cave")
        public int caveMinDistance = 100;
        @Setting("max-distance-cave")
        public int caveMaxDistance = 400;
        @Setting("treasure-2-chance")
        public int treasure2Chance = 30;
        @Setting("bad-regions")
        public String[] badRegions = new String[] { "rcmap", "greed-island" };

        public LocalConfiguration(LootPlugin plugin) {

            super(plugin, "autoplacer.yml");
        }
    }

    public void run(World world, int maxCoordinate, int pointDistance) {

        checkedLocations = 0;

        // print info
        Bukkit.broadcastMessage("Calculate amount of locations...");

        // do spiral
        SpiralCalculator spiralCalculator = new SpiralCalculator(world, maxCoordinate, pointDistance);
        spiralCalculator.doSpiral(0);

        Stack<Location> locations = spiralCalculator.getLocations();
        totalLocations = locations.size();
        Bukkit.broadcastMessage("Will check " + locations.size() + " locations!");

        RepeatingChecker repeatingChecker = new RepeatingChecker(locations, 50);
        checkerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RaidCraft.getComponent(LootPlugin.class), repeatingChecker, 10, 10);
    }

    public class RepeatingChecker implements Runnable {

        private Stack<Location> locations;
        private int amountPerRun;

        private boolean stopped = false;

        public RepeatingChecker(Stack<Location> locations, int amountPerRun) {

            this.locations = locations;
            this.amountPerRun = amountPerRun;
        }

        @Override
        public void run() {

            // skip if no memory available
            Runtime runtime = Runtime.getRuntime();
            int availableMemory = (int)((runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()) / 1048576);
            if(availableMemory < 1512) {
                if(stopped == false) {
                    Bukkit.broadcastMessage("Stopped placement due to low memory! (" + availableMemory + "MB left)");
                }
                stopped = true;
                return;
            }
            if(stopped) {
                stopped = false;
                Bukkit.broadcastMessage("Placement task resumed...");
            }

            for(int i = 0; i < amountPerRun; i++) {

                // cancel task if all location processed
                if(locations.isEmpty()) {
                    Bukkit.getScheduler().cancelTask(checkerTaskId);
                    return;
                }

                Location nextLocation = locations.pop();
                LocationChecker.INST.checkNextLocation(nextLocation);
            }
        }
    }
}
