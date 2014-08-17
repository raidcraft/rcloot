package de.raidcraft.loot.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 13.11.12 - 20:00
 * Description:
 */
public class WorldGuardManager {

    public final static WorldGuardManager INST = new WorldGuardManager();
    private WorldGuardPlugin worldGuard;

    public WorldGuardManager() {

        worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    }

    public List<String> getLocatedRegions(Location location) {

        List<String> regions = new ArrayList<>();
        for (ProtectedRegion region : worldGuard.getRegionManager(location.getWorld()).getApplicableRegions(location)) {
            regions.add(region.getId());
        }
        return regions;
    }

}
