package de.raidcraft.loot.api.table;

import de.raidcraft.api.items.ItemQuality;

/**
 * @author Silthus
 */
public abstract class AbstractLootTableQuality implements LootTableQuality {

    private final int id;
    private final ItemQuality quality;
    private int minAmount;
    private int maxAmount;
    private double chance;

    public AbstractLootTableQuality(int id, ItemQuality quality) {

        this.id = id;
        this.quality = quality;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public ItemQuality getQuality() {

        return quality;
    }

    @Override
    public int getMinAmount() {

        return minAmount;
    }

    @Override
    public void setMinAmount(int minAmount) {

        this.minAmount = minAmount;
    }

    @Override
    public int getMaxAmount() {

        return maxAmount;
    }

    @Override
    public void setMaxAmount(int maxAmount) {

        this.maxAmount = maxAmount;
    }

    @Override
    public double getChance() {

        return chance;
    }

    @Override
    public void setChance(double chance) {

        this.chance = chance;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractLootTableQuality)) return false;

        AbstractLootTableQuality that = (AbstractLootTableQuality) o;

        return quality == that.quality;

    }

    @Override
    public int hashCode() {

        return quality.hashCode();
    }
}
