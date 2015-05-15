package de.raidcraft.loot.api.table;

import de.raidcraft.api.items.ItemQuality;

/**
 * @author Silthus
 */
public interface LootTableQuality {

    int getId();

    ItemQuality getQuality();

    int getMinAmount();

    void setMinAmount(int minAmount);

    int getMaxAmount();

    void setMaxAmount(int maxAmount);

    double getChance();

    void setChance(double chance);

    void save();
}
