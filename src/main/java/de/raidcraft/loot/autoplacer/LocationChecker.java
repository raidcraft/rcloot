package de.raidcraft.loot.autoplacer;

import de.raidcraft.api.database.Database;
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
        if(!Database.getTable(LootObjectsTable.class).isNearLootObject(surface, AutomaticPlacer.INST.config.surfaceMinDistance, 10)) {
            surface.getWorld().getBlockAt(surface).setType(Material.GLOWSTONE);
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
