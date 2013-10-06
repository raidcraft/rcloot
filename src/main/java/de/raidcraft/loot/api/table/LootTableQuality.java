package de.raidcraft.loot.api.table;

import de.raidcraft.api.items.ItemQuality;

/**
 * @author Silthus
 */
public interface LootTableQuality {

    public int getId();

    public ItemQuality getQuality();

    public int getMinAmount();

    public void setMinAmount(int minAmount);

    public int getMaxAmount();

    public void setMaxAmount(int maxAmount);

    public double getChance();

    public void setChance(double chance);

    public void save();
}
