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

    public final static int MAX_ENQUEUD = 50;
    public final static int POINT_DISTANCE = 10;
    public static List<Material> badGroundMaterials = new ArrayList<>();

    public int totalLocations = 0;
    public int checkedLocations = 0;
    public int checkerTaskId = 0;
    public boolean running = false;

    private LocationChecker locationChecker;

    public LocalConfiguration config = new LocalConfiguration(LootPlugin.INST);

    {
        badGroundMaterials.add(Material.WATER);
        badGroundMaterials.add(Material.STATIONARY_WATER);
        badGroundMaterials.add(Material.FENCE);
        badGroundMaterials.add(Material.YELLOW_FLOWER);
        badGroundMaterials.add(Material.RED_ROSE);
        badGroundMaterials.add(Material.GRASS);
        badGroundMaterials.add(Material.LAVA);
        badGroundMaterials.add(Material.STATIONARY_LAVA);
        badGroundMaterials.add(Material.WHEAT);
        badGroundMaterials.add(Material.SUGAR_CANE);
        badGroundMaterials.add(Material.CARROT);
        badGroundMaterials.add(Material.POTATO);
        badGroundMaterials.add(Material.WEB);
        badGroundMaterials.add(Material.PUMPKIN_STEM);
        badGroundMaterials.add(Material.SAPLING);
        badGroundMaterials.add(Material.RAILS);
        badGroundMaterials.add(Material.POWERED_RAIL);
        badGroundMaterials.add(Material.DEAD_BUSH);
        badGroundMaterials.add(Material.RED_MUSHROOM);
        badGroundMaterials.add(Material.BROWN_MUSHROOM);
        badGroundMaterials.add(Material.FIRE);
        badGroundMaterials.add(Material.LADDER);
        badGroundMaterials.add(Material.VINE);
        badGroundMaterials.add(Material.WATER_LILY);
        badGroundMaterials.add(Material.PUMPKIN);
        badGroundMaterials.add(Material.COCOA);
        badGroundMaterials.add(Material.POTATO);
        badGroundMaterials.add(Material.SIGN_POST);
        badGroundMaterials.add(Material.WALL_SIGN);
        badGroundMaterials.add(Material.FENCE_GATE);
    }

    public AutomaticPlacer() {

        locationChecker = new LocationChecker(RaidCraft.getComponent(LootPlugin.class));
        INST = this;
        config.load(true);
    }

    public void resume() {
        if(config.lastRunning) {
            run(Bukkit.getWorld(config.lastWorld), config.lastMaxCoord);
        }
    }

    public void save() {
        if(running) {
            config.lastRunning = running;
            config.lastProcessed = checkedLocations;
            config.save();
        }
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("min-distance-surface")
        public int surfaceMinDistance = 90;
        @Setting("max-distance-surface")
        public int surfaceMaxDistance = 330;
        @Setting("min-distance-cave")
        public int caveMinDistance = 80;
        @Setting("max-distance-cave")
        public int caveMaxDistance = 230;
        @Setting("treasure-2-chance")
        public int treasure2Chance = 30;
        @Setting("bad-regions")
        public String[] badRegions = new String[] { "rcmap", "greed_global", "itemfarm_" };

        // resume information
        @Setting("last-running") public boolean lastRunning = false;
        @Setting("last-processed") public int lastProcessed = 0;
        @Setting("last-max-cord") public int lastMaxCoord = 0;
        @Setting("last-world") public String lastWorld = "";

        public LocalConfiguration(LootPlugin plugin) {

            super(plugin, "autoplacer.yml");
        }
    }

    public void run(World world, int maxCoordinate) {

        checkedLocations = 0;
        config.lastMaxCoord = maxCoordinate;
        config.lastWorld = world.getName();

        // print info
        Bukkit.broadcastMessage("Calculate amount of locations...");

        // do spiral
        SpiralCalculator spiralCalculator = new SpiralCalculator(world, maxCoordinate, POINT_DISTANCE);
        spiralCalculator.doSpiral(0);

        Stack<Location> locations = spiralCalculator.getLocations();
        Stack<Location> flippedStack = new Stack<>();
        while(!locations.isEmpty()) {
            flippedStack.push(locations.pop());
        }
        locations = flippedStack;

        totalLocations = locations.size();

        Bukkit.broadcastMessage("Will check " + locations.size() + " locations!");

        running = true;
        RepeatingChecker repeatingChecker = new RepeatingChecker(locations, MAX_ENQUEUD);
        checkerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RaidCraft.getComponent(LootPlugin.class), repeatingChecker, 10, 10);
    }

    public class RepeatingChecker implements Runnable {

        private Stack<Location> locations;
        private int amountPerRun;

        private boolean stopped = false;

        public RepeatingChecker(Stack<Location> locations, int amountPerRun) {

            this.locations = locations;
            this.amountPerRun = amountPerRun;

            // cut off already processed location if resume
            if(config.lastRunning) {
                config.lastRunning = false;
                config.save();
                checkedLocations = config.lastProcessed;
                for(int i = 0; i < config.lastProcessed; i++) {
                    locations.pop();
                }
            }
        }

        @Override
        public void run() {

            // skip if no memory available
            Runtime runtime = Runtime.getRuntime();
            int availableMemory = (int)((runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory()) / 1048576);
            if(availableMemory < 1512) {
                if(stopped == false) {
                    Bukkit.broadcastMessage("LCAP stopped placement due to low memory! (" + availableMemory + "MB free)");
                }
                stopped = true;
                return;
            }
            if(stopped && availableMemory > 2048) {
                stopped = false;
                Bukkit.broadcastMessage("LCAP placement task resumed (" + availableMemory + "MB free)");
            }
            else if(stopped) {
                return;
            }

            for(int i = 0; i < amountPerRun; i++) {

                // cancel task if all location processed
                if(locations.isEmpty()) {
                    Bukkit.getScheduler().cancelTask(checkerTaskId);
                    running = false;
                    Bukkit.broadcastMessage("LCAP placement finished!");
                    return;
                }

                Location nextLocation = locations.pop();
                locationChecker.checkNextLocation(nextLocation);
            }
        }
    }
}
