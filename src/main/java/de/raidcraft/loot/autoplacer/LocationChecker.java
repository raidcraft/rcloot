package de.raidcraft.loot.autoplacer;

import de.raidcraft.api.database.Database;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.database.tables.LootObjectsTable;
import de.raidcraft.loot.util.WorldGuardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author Philip
 */
public class LocationChecker {

    public final static LocationChecker INST = new LocationChecker();

    public void checkNextLocation(Location location) {

        int distance;

        // check if bad region
        for(String region : WorldGuardManager.INST.getLocatedRegions(location)) {
            for(String badRegion : AutomaticPlacer.INST.config.badRegions){
                if(region.equalsIgnoreCase(badRegion)) {
                    return;
                }
            }
        }

        /* surface: */

        // get potential surface chest location
        Location surface = location.getWorld().getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation();

        // check if other chests too close
        distance = (int) (Math.random()
                * (AutomaticPlacer.INST.config.surfaceMaxDistance - AutomaticPlacer.INST.config.surfaceMinDistance)
                +  AutomaticPlacer.INST.config.surfaceMinDistance);
        if(!Database.getTable(LootObjectsTable.class).isNearLootObject(surface, distance, 20)) {
            int treasureLevel = 1;
            int chance = (int) (Math.random() * 100F);
            if(AutomaticPlacer.INST.config.treasure2Chance > chance) {
                treasureLevel = 2;
            }

            surface.getBlock().setType(Material.CHEST);
            LootFactory.inst.createTreasureLootObject("AutomaticPlacer", surface.getBlock(), treasureLevel, false);
        }

        /* cave: */

//        // get potential cave chest locations
//        List<Location> caveLocations = new ArrayList<>();
//
//
//        // check if other chests too close
//        if(Database.getTable(LootObjectsTable.class).isNearLootObject(location, AutomaticPlacer.INST.config.caveMinDistance, 20)) {
//            return;
//        }

        // info
        AutomaticPlacer.INST.checkedLocations++;
        if(AutomaticPlacer.INST.checkedLocations % 100 == 0) {
            Bukkit.broadcastMessage("Already checked " + AutomaticPlacer.INST.checkedLocations + " / " + AutomaticPlacer.INST.totalLocations);
        }
    }
}
