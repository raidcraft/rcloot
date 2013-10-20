package de.raidcraft.loot;

import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.items.ItemQuality;
import de.raidcraft.api.items.ItemType;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class RandomLootTableConfig extends ConfigurationBase<LootPlugin> {

    public RandomLootTableConfig(LootPlugin plugin, File file) {

        super(plugin, file);
    }

    public int getUpperLevelDiff() {

        return getInt("upper-diff", 3);
    }

    public int getLowerLevelDiff() {

        return getInt("lower-diff", 0);
    }

    public int getMinLoot() {

        return getInt("min-loot", 1);
    }

    public int getMaxLoot() {

        return getInt("max-loot", getMinLoot());
    }

    public Map<ItemType, Double> getItemTypes() {

        Map<ItemType, Double> types = new HashMap<>();
        ConfigurationSection section = getConfigurationSection("item-types");
        if (section != null) {
            for (String type : section.getKeys(false)) {
                types.put(ItemType.fromString(type), section.getDouble(type));
            }
        }
        return types;
    }

    public Map<ItemQuality, Double> getItemQualities() {

        Map<ItemQuality, Double> qualities = new HashMap<>();
        ConfigurationSection section = getConfigurationSection("item-qualities");
        if (section != null) {
            for (String type : section.getKeys(false)) {
                qualities.put(ItemQuality.fromString(type), section.getDouble(type));
            }
        }
        return qualities;
    }
}
