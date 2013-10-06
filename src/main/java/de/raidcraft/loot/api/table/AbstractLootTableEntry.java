package de.raidcraft.loot.api.table;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 11.10.12 - 20:19
 * Description:
 */
public abstract class AbstractLootTableEntry implements LootTableEntry {

    private final int id;
    private ItemStack item;
    private int minAmount;
    private int maxAmount;
    private double chance;

    public AbstractLootTableEntry(int id) {

        this.id = id;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public ItemStack getItem() {

        return item.clone();
    }

    @Override
    public void setItem(ItemStack item) {

        this.item = item.clone();
    }

    @Override
    public void setItem(String id) {

        try {
            this.item = RaidCraft.getItem(id);
        } catch (CustomItemException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
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

        if (chance > 1.0) {
            chance = 1.0;
        } else if (chance < 0.0) {
            chance = 0.0;
        }
        this.chance = chance;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractLootTableEntry)) return false;

        AbstractLootTableEntry that = (AbstractLootTableEntry) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {

        return id;
    }
}
