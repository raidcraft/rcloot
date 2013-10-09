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
public class QualityLootTable {

    private final int amount;
    private final ItemQuality quality;
    private List<QualityLootTableEntry> entries = new ArrayList<>();

    public QualityLootTable(ItemQuality quality, List<LootTableEntry> entries, int amount) {

        this.amount = amount;
        this.quality = quality;
        List<LootTableEntry> matchingEntires = new ArrayList<>();
        double totalChance = 0.0;
        // first we sort out entries that dont match as custom items or our quality
        for (LootTableEntry entry : entries) {
            if (CustomItemUtil.isCustomItem(entry.getItem()) && RaidCraft.getCustomItem(entry.getItem()).getItem().getQuality() == quality) {
                matchingEntires.add(entry);
                totalChance += entry.getChance();
            }
        }
        double finalChance = 0.0;
        // now we need to calculate the relative chance of each entry related to the other entries
        for (LootTableEntry entry : matchingEntires) {
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

    public List<LootTableEntry> loot() {

        ArrayList<LootTableEntry> entries = new ArrayList<>();
        double random = Math.random();
        int added = 0;
        for (QualityLootTableEntry entry : this.entries) {
            if (amount < added) {
                break;
            }
            if (entry.isWithinRange(random)) {
                entries.add(entry.getEntry());
                added++;
            }
        }
        return entries;
    }
}
