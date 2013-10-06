package de.raidcraft.loot.api.table;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.ItemQuality;
import de.raidcraft.util.MathUtil;
import org.bukkit.inventory.ItemStack;

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
            this.qualities.put(quality.getQuality(), quality);
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

        // lets set up our quality checks :D
        Map<ItemQuality, Integer> qualityAmounts = new EnumMap<>(ItemQuality.class);
        Map<ItemQuality, Integer> addedQualityAmounts = new EnumMap<>(ItemQuality.class);
        if (!qualities.isEmpty()) {
            for (LootTableQuality quality : getQualities()) {
                if (Math.random() < quality.getChance()) {
                    qualityAmounts.put(quality.getQuality(), MathUtil.RANDOM.nextInt(quality.getMaxAmount()) + quality.getMinAmount());
                }
            }
        }

        List<LootTableEntry> loot = new ArrayList<>();
        List<LootTableEntry> lootTableEntries = getEntries();
        // bring some randomness into the loot seleciton
        Collections.shuffle(lootTableEntries);

        if (lootTableEntries.isEmpty()) {
            return loot;
        }

        int lootAmount = (int) (Math.random() * (getMaxLootItems() - getMinLootItems()) + getMinLootItems());

        for (LootTableEntry entry : lootTableEntries) {
            // lets check if we reached our wanted loot amount
            if (lootAmount < loot.size()) {
                break;
            }

            ItemStack item = entry.getItem();
            // we now need to check the the chance of the individual qualities and then the chance of the item
            // so if a loot table has a chance of 10% to drop rare items and hits that 10%
            // and then also checks the dropchance of each item of that quality
            if (!qualityAmounts.isEmpty() && RaidCraft.isCustomItem(item)) {
                ItemQuality itemQuality = RaidCraft.getCustomItem(item).getItem().getQuality();
                if (!qualityAmounts.containsKey(itemQuality)) {
                    // we dont add this item quality into the loot table
                    continue;
                }
                // the quality was already added once, lets check if we can add more
                if (addedQualityAmounts.containsKey(itemQuality) && qualityAmounts.get(itemQuality) < addedQualityAmounts.get(itemQuality)) {
                    // we have already added enough items of this quality, lets go on
                    continue;
                }
                // now we are good to go and need to check the chance of our item
                if (Math.random() < entry.getChance()) {
                    // lets add the item
                    loot.add(entry);
                    // also increase the added quality amount
                    if (!addedQualityAmounts.containsKey(itemQuality)) {
                        addedQualityAmounts.put(itemQuality, 1);
                    } else {
                        addedQualityAmounts.put(itemQuality, addedQualityAmounts.get(itemQuality) + 1);
                    }
                }
            } else {
                // here we simply check the chance of an item to be dropped and add it
                if (Math.random() < entry.getChance()) {
                    loot.add(entry);
                }
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
