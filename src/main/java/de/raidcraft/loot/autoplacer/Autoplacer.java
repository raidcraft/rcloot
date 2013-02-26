package de.raidcraft.loot.autoplacer;

import de.raidcraft.api.Component;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.loot.LootPlugin;

/**
 * @author Philip
 */
public class Autoplacer implements Component {
    public LocalConfiguration config = new LocalConfiguration(LootPlugin.INST);

    public Autoplacer() {
        config.load(true);
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
}
