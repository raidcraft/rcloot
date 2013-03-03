package de.raidcraft.loot.autoplacer;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Stack;

/**
 * @author Philip
 */
public class AutomaticPlacer implements Component {

    public static AutomaticPlacer INST = null;

    public final static int MAX_ENQUEUD = 100;
    public int totalLocations = 0;
    public int checkedLocations = 0;
    public int checkerTaskId = 0;

    public LocalConfiguration config = new LocalConfiguration(LootPlugin.INST);

    public AutomaticPlacer() {
        INST = this;
        config.load(true);
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("min-distance-surface")
        public int surfaceMinDistance = 0;
        @Setting("max-distance-surface")
        public int surfaceMaxDistance = 0;
        @Setting("min-distance-cave")
        public int caveMinDistance = 0;
        @Setting("max-distance-cave")
        public int caveMaxDistance = 0;
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

        RepeatingChecker repeatingChecker = new RepeatingChecker(locations, 250);
        checkerTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(RaidCraft.getComponent(LootPlugin.class), repeatingChecker, 10, 10);
    }

    public class RepeatingChecker implements Runnable {

        private Stack<Location> locations;
        private int amountPerRun;

        public RepeatingChecker(Stack<Location> locations, int amountPerRun) {

            this.locations = locations;
            this.amountPerRun = amountPerRun;
        }

        @Override
        public void run() {

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
