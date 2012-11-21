package de.raidcraft.loot.table;

import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 11.10.12 - 20:19
 * Description:
 */
public class SimpleLootTableEntry implements LootTableEntry {

    private int id = 0;
    private ItemStack item;
    private int chance;

    @Override
    public void setId(int id) {

        this.id = id;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public ItemStack getItem() {

        return item;
    }

    @Override
    public void setItem(ItemStack item) {

        this.item = new ItemStack(item.getType(), item.getAmount(), item.getDurability());
    }

    @Override
    public int getChance() {

        return chance;
    }

    @Override
    public void setChance(int chance) {

        if (chance > 100) {
            chance = 100;
        }
        if (chance < 0) {
            chance = 0;
        }
        this.chance = chance;
    }
}
