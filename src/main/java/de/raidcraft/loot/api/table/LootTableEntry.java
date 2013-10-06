package de.raidcraft.loot.api.table;

import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:38
 * Description:
 */
public interface LootTableEntry {

    public int getId();

    public ItemStack getItem();

    public void setItem(ItemStack item);

    public void setItem(String id);

    public int getAmount();

    public void setAmount(int amount);

    public double getChance();

    public void setChance(double chance);

    public void save();
}
