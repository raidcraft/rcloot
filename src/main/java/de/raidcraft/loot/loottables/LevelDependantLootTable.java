package de.raidcraft.loot.loottables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItem;
import de.raidcraft.api.items.CustomItemManager;
import de.raidcraft.api.items.ItemQuality;
import de.raidcraft.api.items.ItemType;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.AbstractLootTable;
import de.raidcraft.loot.api.table.AbstractLootTableEntry;
import de.raidcraft.loot.api.table.AbstractLootTableQuality;

import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class LevelDependantLootTable extends AbstractLootTable {

    private final int level;

    public LevelDependantLootTable(int level) {

        super(-1);
        this.level = level;
        LootPlugin.LocalConfiguration config = RaidCraft.getComponent(LootPlugin.class).config;
        setMinMaxLootItems(config.levelTableMinLoot, config.levelTableMaxLoot);

        Map<ItemQuality, Double> qualities = config.getLevelTableItemQualities();
        for (ItemQuality quality : qualities.keySet()) {
            AbstractLootTableQuality tableQuality = new AbstractLootTableQuality(-1, quality) {
                @Override
                public void save() {
                    // dont save
                }
            };
            tableQuality.setChance(qualities.get(quality));
            tableQuality.setMinAmount(1);
            tableQuality.setMaxAmount(1);
            addQuality(tableQuality);
        }

        Map<ItemType, Double> itemTypes = config.getLevelTableItemTypes();
        List<CustomItem> items = RaidCraft.getComponent(CustomItemManager.class).getLoadedCustomItems();
        for (CustomItem item : items) {
            if (itemTypes.containsKey(item.getType()) && qualities.containsKey(item.getQuality())) {
                // lets check the item level
                if (item.getItemLevel() > getLevel() - config.levelTableLowerLevelDiff && item.getItemLevel() < getLevel() + config.levelTableUpperDiff) {
                    AbstractLootTableEntry entry = new AbstractLootTableEntry(-1) {
                        @Override
                        public void save() {
                            // dont save
                        }
                    };
                    entry.setChance(itemTypes.get(item.getType()));
                    entry.setItem(item.createNewItem());
                    addEntry(entry);
                }
            }
        }
    }

    public int getLevel() {

        return level;
    }

    @Override
    public void save() {
        // non persistant
    }

    @Override
    public void delete() {
        // non persistant
    }
}
