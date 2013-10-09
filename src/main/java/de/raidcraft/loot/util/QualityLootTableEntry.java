package de.raidcraft.loot.util;

import de.raidcraft.loot.api.table.LootTableEntry;

/**
 * @author Silthus
 */
public class QualityLootTableEntry {

    private final LootTableEntry entry;
    private final double minChance;
    private final double maxChance;

    public QualityLootTableEntry(LootTableEntry entry, double minChance, double maxChance) {

        this.entry = entry;
        this.minChance = minChance;
        this.maxChance = maxChance;
    }

    public LootTableEntry getEntry() {

        return entry;
    }

    public double getMinChance() {

        return minChance;
    }

    public double getMaxChance() {

        return maxChance;
    }

    public boolean isWithinRange(double random) {

        return random > minChance && random <= maxChance;
    }
}
