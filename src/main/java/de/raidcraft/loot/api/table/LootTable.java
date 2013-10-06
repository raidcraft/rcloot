package de.raidcraft.loot.api.table;

import de.raidcraft.api.items.ItemQuality;

import java.util.List;
import java.util.Set;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:26
 * Description:
 */
public interface LootTable {

    public int getId();

    public void setEntries(List<LootTableEntry> entries);

    public void addEntry(LootTableEntry entry);

    public boolean removeEntry(LootTableEntry entry);

    public List<LootTableEntry> getEntries();

    public LootTableQuality getQuality(ItemQuality quality);

    public void setQualities(Set<LootTableQuality> qualities);

    public void addQuality(LootTableQuality quality);

    public LootTableQuality removeQuality(LootTableQuality quality);

    public Set<LootTableQuality> getQualities();

    public void setMinMaxLootItems(int min, int max);

    public void setMinLootItems(int min);

    public int getMinLootItems();

    public void setMaxLootItems(int max);

    public int getMaxLootItems();

    public List<LootTableEntry> loot();

    public void save();

    public void delete();
}
