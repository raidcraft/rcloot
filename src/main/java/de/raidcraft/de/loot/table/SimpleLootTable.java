package de.raidcraft.de.loot.table;

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
        List<LootTableEntry> lootSelected = new ArrayList<>();
        List<LootTableEntry> loot = new ArrayList<>();
        int rnd = (int)(Math.random() * (100.));
        int i = 0;

        // if all items should be loot
        if(minLootItems == getEntries().size()) {
            lootSelected = getEntries();
        } else {
            while(lootSelected.size() < minLootItems) {
                for(LootTableEntry entry : getEntries()) {
                    if(rnd-i <= entry.getChance()) {
                        lootSelected.add(entry);
                    }
                }
                i += 5; //increase chance to prevent infinite loop
            }
        }

       // limit items and shuffle list
       int numItems = (int)(Math.random() * ((double)maxLootItems - (double)minLootItems) + (double)minLootItems);
       for(i = 0; i < numItems; i++) {
           loot.add(lootSelected.get((int)(Math.random() * (double)lootSelected.size())));
       }

       return loot;
    }
}
