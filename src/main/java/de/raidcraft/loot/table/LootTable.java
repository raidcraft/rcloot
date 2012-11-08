package de.raidcraft.loot.table;

import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:26
 * Description:
 */
public interface LootTable {

    public void setId(int id);
    
    public int getId();
    
    public void setEntries(List<LootTableEntry> entries);
    
    public void addEntry(LootTableEntry entry);

    public void removeEntry(LootTableEntry entry);

    public List<LootTableEntry> getEntries();

    public void setMinMaxLootItems(int min, int max);

    public void setMinLootItems(int min);
    
    public int getMinLootItems();
    
    public void setMaxLootItems(int max);
    
    public int getMaxLootItems();
    
    public List<LootTableEntry> loot();
}
