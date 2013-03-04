package de.raidcraft.loot.autoplacer;

import de.raidcraft.api.database.Database;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.util.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip
 */
public class LocationChecker {

    public final static LocationChecker INST = new LocationChecker();

    public void checkNextLocation(Location location) {

        int distance;
        int treasureLevel;
        int chance;

        // check if bad region
        for(String region : WorldGuardManager.INST.getLocatedRegions(location)) {
            for(String badRegion : AutomaticPlacer.INST.config.badRegions){
                if(region.equalsIgnoreCase(badRegion)) {
                    return;
                }
            }
        }

        /* surface: */

        // get possible surface chest location
        Location surfaceLocation = location.getWorld().getHighestBlockAt(location).getLocation();
        if(!AutomaticPlacer.badGroundMaterials.contains(surfaceLocation.getBlock().getRelative(0, -1, 0).getType())) {

            // check if other chests too close
            distance = (int) (Math.random()
                    * (AutomaticPlacer.INST.config.surfaceMaxDistance - AutomaticPlacer.INST.config.surfaceMinDistance)
                    +  AutomaticPlacer.INST.config.surfaceMinDistance);
            if(!Database.getTable(LootObjectsTable.class).isNearLootObject(surfaceLocation, distance, 25)) {
                treasureLevel = 1;
                chance = (int) (Math.random() * 100F);
                if(AutomaticPlacer.INST.config.treasure2Chance > chance) {
                    treasureLevel = 2;
                }

                surfaceLocation.getBlock().setType(Material.CHEST);
                LootFactory.inst.createTreasureLootObject("AutomaticPlacerSurface", surfaceLocation.getBlock(), treasureLevel, false);
            }
        }

        /* cave: */

        // get possible cave chest locations
        List<Location> caveLocations = new ArrayList<>();
        Block targetBlock = location.getWorld().getHighestBlockAt(location).getRelative(0, -10, 0);
        while(targetBlock.getLocation().getBlockY() > 1) {

            targetBlock = targetBlock.getRelative(0, -1, 0);

            // check if above is air and below hard ground
            if(targetBlock.getType() == Material.AIR
                    && targetBlock.getRelative(0, 1, 0).getType() == Material.AIR
                    && targetBlock.getRelative(0, -1, 0).getType() != Material.AIR
                    && targetBlock.getRelative(0, -1, 0).getType() != Material.WATER) {
                caveLocations.add(targetBlock.getLocation().clone());
            }
        }

        // select one of the possible locations
        if(caveLocations.size() > 0) {
            int randPosition = (int)(Math.random() * caveLocations.size());
            Location caveLocation = caveLocations.get(randPosition);

            distance = (int) (Math.random()
                    * (AutomaticPlacer.INST.config.caveMaxDistance - AutomaticPlacer.INST.config.caveMinDistance)
                    +  AutomaticPlacer.INST.config.caveMinDistance);

            if(!Database.getTable(LootObjectsTable.class).isNearLootObject(caveLocation, distance, 10)) {
                treasureLevel = 1;
                chance = (int) (Math.random() * 100F);
                if(AutomaticPlacer.INST.config.treasure2Chance > chance) {
                    treasureLevel = 2;
                }
                caveLocation.getBlock().setType(Material.CHEST);
                LootFactory.inst.createTreasureLootObject("AutomaticPlacerCave", caveLocation.getBlock(), treasureLevel, false);
            }
        }

        // info
        AutomaticPlacer.INST.checkedLocations++;
        if(AutomaticPlacer.INST.checkedLocations % 100 == 0) {
            double percentage = (double)Math.round(((double)AutomaticPlacer.INST.checkedLocations / (double)AutomaticPlacer.INST.totalLocations) * 10000.) / 100.;
            Bukkit.broadcastMessage("Processed: " + AutomaticPlacer.INST.checkedLocations + " / " + AutomaticPlacer.INST.totalLocations
                    + " (" + percentage + "%)");
        }
    }
}
