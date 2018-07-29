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
    private int amount;
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

        ItemStack clone = item.clone();
        clone.setAmount(getAmount());
        return clone;
    }

    @Override
    public void setItem(ItemStack item) {

        this.item = item.clone();
        setAmount(item.getAmount());
    }

    @Override
    public void setItem(String id) {

        try {
            this.item = RaidCraft.getSafeItem(id);
        } catch (CustomItemException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            delete();
        }
    }

    @Override
    public int getAmount() {

        return amount;
    }

    @Override
    public void setAmount(int amount) {

        this.amount = amount;
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

    protected abstract void delete();

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

    @Override
    public String toString() {
        return amount + "x "
                + item.getItemMeta().getDisplayName()
                + " (" + chance + ")";
    }
}