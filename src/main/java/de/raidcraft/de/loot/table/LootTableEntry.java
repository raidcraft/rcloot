package de.raidcraft.de.loot.table;

import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:38
 * Description:
 */
public interface LootTableEntry {

    public void setId(int id);

    public int getId();

    public ItemStack getItem();
    
    public void setItem(ItemStack item);
    
    public int getChance();
    
    public void setChance(int chance);
}
