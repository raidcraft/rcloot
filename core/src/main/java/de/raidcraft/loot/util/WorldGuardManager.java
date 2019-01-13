package de.raidcraft.loot.util;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<ApplicableRegionSet> worldGuardRegions = LocationUtil.getWorldGuardRegions(location);
        if (!worldGuardRegions.isPresent()) return regions;

        for (ProtectedRegion region : worldGuardRegions.get().getRegions()) {
            regions.add(region.getId());
        }
        return regions;
    }

}
