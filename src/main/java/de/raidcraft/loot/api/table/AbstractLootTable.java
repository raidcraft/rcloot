package de.raidcraft.loot.api.table;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.ItemQuality;
import de.raidcraft.loot.util.QualityLootTable;
import de.raidcraft.util.CustomItemUtil;
import de.raidcraft.util.MathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractLootTable implements LootTable {

    private final int id;
    private final List<LootTableEntry> entries = new ArrayList<>();
    private final Map<ItemQuality, LootTableQuality> qualities = new EnumMap<>(ItemQuality.class);
    private int minLoot;
    private int maxLoot;

    public AbstractLootTable(int id) {

        this.id = id;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public void setEntries(List<LootTableEntry> entries) {

        this.entries.clear();
        this.entries.addAll(entries);
    }

    @Override
    public void addEntry(LootTableEntry entry) {

        entries.add(entry);
    }

    @Override
    public boolean removeEntry(LootTableEntry entry) {

        return entries.remove(entry);
    }

    @Override
    public List<LootTableEntry> getEntries() {

        return new ArrayList<>(entries);
    }

    @Override
    public LootTableQuality getQuality(ItemQuality quality) {

        return qualities.get(quality);
    }

    @Override
    public Set<LootTableQuality> getQualities() {

        return new HashSet<>(qualities.values());
    }

    protected boolean hasQuality(ItemQuality quality) {

        for (LootTableQuality lootTableQuality : getQualities()) {
            if (quality == lootTableQuality.getQuality()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LootTableQuality removeQuality(LootTableQuality quality) {

        return qualities.remove(quality.getQuality());
    }

    @Override
    public void addQuality(LootTableQuality quality) {

        qualities.put(quality.getQuality(), quality);
    }

    @Override
    public void setQualities(Set<LootTableQuality> qualities) {

        this.qualities.clear();
        for (LootTableQuality quality : qualities) {
            addQuality(quality);
        }
    }

    @Override
    public void setMinMaxLootItems(int min, int max) {

        setMinLootItems(min);
        setMaxLootItems(max);
    }

    @Override
    public void setMinLootItems(int min) {

        this.minLoot = min;
    }

    @Override
    public int getMinLootItems() {

        return minLoot;
    }

    @Override
    public void setMaxLootItems(int max) {

        this.maxLoot = max;
    }

    @Override
    public int getMaxLootItems() {

        return maxLoot;
    }

    @Override
    public List<LootTableEntry> loot() {

        List<LootTableEntry> lootTableEntries = getEntries();
        // lets set up our quality checks :D
        List<QualityLootTable> qualityLootTables = new ArrayList<>();
        if (!qualities.isEmpty()) {
            for (LootTableQuality quality : getQualities()) {
                if (Math.random() < quality.getChance()) {
                    int amount = quality.getMaxAmount() == quality.getMinAmount() ?
                            quality.getMaxAmount() : MathUtil.RANDOM.nextInt(quality.getMaxAmount()) + quality.getMinAmount();
                    QualityLootTable qualityLootTable = new QualityLootTable(quality.getQuality(),
                            getEntries(),
                            amount);
                    qualityLootTables.add(qualityLootTable);
                    lootTableEntries.removeAll(qualityLootTable.getLootTableEntries());
                }
            }
        }
        Collections.sort(qualityLootTables);
        Collections.reverse(qualityLootTables);

        List<LootTableEntry> loot = new ArrayList<>();
        // bring some randomness into the loot seleciton
        Collections.shuffle(lootTableEntries);

        if (lootTableEntries.isEmpty() && qualityLootTables.isEmpty()) {
            return loot;
        }

        int lootAmount = getMaxLootItems() == getMinLootItems() ?
                getMaxLootItems() : MathUtil.RANDOM.nextInt(getMaxLootItems()) + getMinLootItems();

        for (LootTableEntry entry : lootTableEntries) {
            if (lootAmount <= loot.size()) {
                break;
            }
            if (CustomItemUtil.isCustomItem(entry.getItem())) {
                CustomItemStack customItem = RaidCraft.getCustomItem(entry.getItem());
                if (hasQuality(customItem.getItem().getQuality())) {
                    continue;
                }
            }
            if (Math.random() < entry.getChance()) {
                loot.add(entry);
            }
        }

        if (!qualityLootTables.isEmpty()) {
            // loot our quality tables
            for (QualityLootTable lootTable : qualityLootTables) {
                if (lootAmount <= loot.size()) {
                    break;
                }
                loot.addAll(lootTable.loot());
            }
        }

        return loot;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractLootTable)) return false;

        AbstractLootTable that = (AbstractLootTable) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {

        return id;
    }
}
