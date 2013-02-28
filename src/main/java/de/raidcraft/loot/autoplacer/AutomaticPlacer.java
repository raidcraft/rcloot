package de.raidcraft.loot.autoplacer;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.autoplacer.listener.NextLocationListener;
import org.bukkit.World;

/**
 * @author Philip
 */
public class AutomaticPlacer implements Component {

    public static AutomaticPlacer INST = null;

    public LocalConfiguration config = new LocalConfiguration(LootPlugin.INST);

    public AutomaticPlacer(BasePlugin plugin) {
        INST = this;
        config.load(true);
        plugin.registerEvents(new NextLocationListener());
    }

    public class LocalConfiguration extends ConfigurationBase<LootPlugin> {

        @Setting("treasure-1-amount-surface")
        public int treasure1AmountSurface = 0;
        @Setting("treasure-2-amount-surface")
        public int treasure2AmountSurface = 0;
        @Setting("treasure-1-amount-caves")
        public int treasure1AmountCaves = 0;
        @Setting("treasure-2-amount-caves")
        public int treasure2AmountCaves = 0;

        public LocalConfiguration(LootPlugin plugin) {

            super(plugin, "autoplacer.yml");
        }
    }

    public void run(World world, int maxCoordinate, int pointDistance) {

        SpiralChecker spiralChecker = new SpiralChecker(world, maxCoordinate, pointDistance);
        spiralChecker.doRecursiveSpiral(0);
    }
}
