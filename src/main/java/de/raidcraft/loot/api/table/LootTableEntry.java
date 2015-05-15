package de.raidcraft.loot.api.table;

import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:38
 * Description:
 */
public interface LootTableEntry {

    int getId();

    ItemStack getItem();

    void setItem(ItemStack item);

    void setItem(String id);

    int getAmount();

    void setAmount(int amount);

    double getChance();

    void setChance(double chance);

    void save();
}
