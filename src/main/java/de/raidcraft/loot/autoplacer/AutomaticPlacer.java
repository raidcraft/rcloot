package de.raidcraft.loot.autoplacer;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class AutomaticPlacer implements Component {

    public static AutomaticPlacer INST = null;

    public final static int MAX_ENQUEUD = 100;

    public LocalConfiguration config = new LocalConfiguration(LootPlugin.INST);

    private List<CreatedChestInformation> createdChestInformation = new ArrayList<>();

    public AutomaticPlacer() {
        INST = this;
        config.load(true);
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("min-distance-surface")
        public int surfaceMinDistance = 0;
        @Setting("min-distance-cave")
        public int caveMinDistance = 0;
        @Setting("treasure-2-chance")
        public int treasure2Chance = 30;
        @Setting("bad-regions")
        public String[] badRegions;

        public LocalConfiguration(LootPlugin plugin) {

            super(plugin, "autoplacer.yml");
        }
    }

    public void run(World world, int maxCoordinate, int pointDistance) {

        // do spiral asynchronously
        SpiralTask spiralTask = new SpiralTask(world, maxCoordinate, pointDistance);
        Bukkit.getScheduler().runTaskAsynchronously(RaidCraft.getComponent(LootPlugin.class), spiralTask);

        // check each two seconds if new cheats must be created
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RaidCraft.getComponent(LootPlugin.class), new Runnable() {
            @Override
            public void run() {
                List<CreatedChestInformation> information = getCreatedChestInformation();
                for(CreatedChestInformation entry : information) {
                    entry.getLocation().getWorld().getBlockAt(entry.getLocation()).setType(Material.GLOWSTONE);
                }
            }
        }, 2*20, 2*20);
    }

    public synchronized List<CreatedChestInformation> getCreatedChestInformation() {
        List<CreatedChestInformation> copy = createdChestInformation;
        createdChestInformation = new ArrayList<>();
        return copy;
    }

    public synchronized boolean addCreatedChestInformatin(CreatedChestInformation info) {

        if(createdChestInformation.size() > MAX_ENQUEUD) {
            return false;
        }
        else {
            createdChestInformation.add(info);
            return true;
        }
    }
}
