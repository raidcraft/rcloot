package de.raidcraft.loot.loottables;

import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSObjectCreator;
import de.raidcraft.api.random.RDSObjectFactory;
import de.raidcraft.items.loottables.FilteredItemsTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LevelDependantLootTable extends FilteredItemsTable implements RDSObjectCreator {

    @RDSObjectFactory.Name("level-dependent-items")
    public static class Factory implements RDSObjectFactory{

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new LevelDependantLootTable(config, config.getInt("level", 1));
        }

    }

    private int lowerDiff;
    private int upperDiff;

    public LevelDependantLootTable(ConfigurationSection config, int level) {

        this(config.getInt("lower-diff", 0), config.getInt("upper-diff", 0), level);
    }

    public LevelDependantLootTable(int lowerDiff, int upperDiff, int level) {

        super(level - lowerDiff, level + upperDiff);
        this.lowerDiff = lowerDiff;
        this.upperDiff = upperDiff;
    }

    @Override
    public LevelDependantLootTable createInstance() {

        LevelDependantLootTable table = new LevelDependantLootTable(lowerDiff, upperDiff, 1);
        table.minItemLevel = minItemLevel;
        table.maxItemLevel = maxItemLevel;
        table.itemTypes = itemTypes;
        table.itemQualities = itemQualities;
        table.bindTypes = bindTypes;
        table.includeCategories = includeCategories;
        table.excludeCategories = excludeCategories;
        table.itemIds = itemIds;
        table.nameFilter = nameFilter;
        table.idFilterMin = idFilterMin;
        table.idFilterMax = idFilterMax;
        table.ignoreUnlootable = ignoreUnlootable;
        return table;
    }

    public void setLevel(int level) {

        this.minItemLevel = level - lowerDiff;
        this.maxItemLevel = level - upperDiff;
    }
}