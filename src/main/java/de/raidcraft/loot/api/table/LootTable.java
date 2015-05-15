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

    int getId();

    void setEntries(List<LootTableEntry> entries);

    void addEntry(LootTableEntry entry);

    boolean removeEntry(LootTableEntry entry);

    List<LootTableEntry> getEntries();

    LootTableQuality getQuality(ItemQuality quality);

    void setQualities(Set<LootTableQuality> qualities);

    void addQuality(LootTableQuality quality);

    LootTableQuality removeQuality(LootTableQuality quality);

    Set<LootTableQuality> getQualities();

    void setMinMaxLootItems(int min, int max);

    void setMinLootItems(int min);

    int getMinLootItems();

    void setMaxLootItems(int max);

    int getMaxLootItems();

    List<LootTableEntry> loot();

    void save();

    void delete();
}
