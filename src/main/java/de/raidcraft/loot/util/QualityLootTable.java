package de.raidcraft.loot.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.ItemQuality;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.util.CustomItemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class QualityLootTable implements Comparable<QualityLootTable> {

    private final int amount;
    private final ItemQuality quality;
    private List<QualityLootTableEntry> entries = new ArrayList<>();
    private List<LootTableEntry> lootTableEntries = new ArrayList<>();

    public QualityLootTable(ItemQuality quality, List<LootTableEntry> entries, int amount) {

        this.amount = amount;
        this.quality = quality;
        double totalChance = 0.0;
        // first we sort out entries that dont match as custom items or our quality
        for (LootTableEntry entry : entries) {
            if (CustomItemUtil.isCustomItem(entry.getItem()) && RaidCraft.getCustomItem(entry.getItem()).getItem().getQuality() == quality) {
                lootTableEntries.add(entry);
                totalChance += entry.getChance();
            }
        }
        double finalChance = 0.0;
        // now we need to calculate the relative chance of each entry related to the other entries
        for (LootTableEntry entry : lootTableEntries) {
            double relativeChance = entry.getChance() / totalChance;
            this.entries.add(new QualityLootTableEntry(entry, finalChance, finalChance += relativeChance));
        }
    }

    public int getAmount() {

        return amount;
    }

    public ItemQuality getQuality() {

        return quality;
    }

    public List<QualityLootTableEntry> getEntries() {

        return entries;
    }

    public List<LootTableEntry> getLootTableEntries() {

        return lootTableEntries;
    }

    public List<LootTableEntry> loot() {

        ArrayList<LootTableEntry> entries = new ArrayList<>();
        int added = 0;
        double random = Math.random();
        for (QualityLootTableEntry entry : this.entries) {
            if (getAmount() <= added) {
                break;
            }
            if (entry.isWithinRange(random)) {
                entries.add(entry.getEntry());
                added++;
                random = Math.random();
            }
        }
        return entries;
    }

    @Override
    public int compareTo(QualityLootTable o) {

        return o.getQuality().compareTo(getQuality());
    }
}
