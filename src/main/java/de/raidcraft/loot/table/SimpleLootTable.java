package de.raidcraft.loot.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 20:18
 * Description:
 */
public class SimpleLootTable implements LootTable {

    private int id = 0;
    private List<LootTableEntry> entries = new ArrayList<>();
    private int minLootItems;
    private int maxLootItems;

    @Override
    public void setId(int id) {

        this.id = id;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public void setEntries(List<LootTableEntry> entries) {

        this.entries = entries;
    }

    @Override
    public void addEntry(LootTableEntry entry) {

        entries.add(entry);
    }

    @Override
    public void removeEntry(LootTableEntry entry) {

        entries.remove(entry);
    }

    @Override
    public List<LootTableEntry> getEntries() {

        return entries;
    }

    @Override
    public void setMinMaxLootItems(int min, int max) {

        this.minLootItems = min;
        this.maxLootItems = max;
    }

    @Override
    public void setMinLootItems(int min) {

        this.minLootItems = min;
    }

    @Override
    public int getMinLootItems() {

        return minLootItems;
    }

    @Override
    public void setMaxLootItems(int max) {

        this.maxLootItems = max;
    }

    @Override
    public int getMaxLootItems() {

        return maxLootItems;
    }

    @Override
    public List<LootTableEntry> loot() {

        List<LootTableEntry> loot = new ArrayList<>();
        if (getEntries().size() == 0) {
            return loot;
        }
        int lootAmount = (int) (Math.random() * (maxLootItems - minLootItems) + minLootItems);

        for (int i = 0; i < lootAmount; i++) {

            LootTableEntry selected = getEntries().get((int) (Math.random() * getEntries().size()));
            int j = 0;

            while (loot.contains(selected) && loot.size() < getEntries().size()) {
                selected = getEntries().get(j);
                j++;
            }
            loot.add(selected);
        }

        return loot;
    }
}
