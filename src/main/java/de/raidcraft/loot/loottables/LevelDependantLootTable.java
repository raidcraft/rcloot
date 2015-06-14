package de.raidcraft.loot.loottables;

import de.raidcraft.api.random.RDSObject;
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
public class LevelDependantLootTable extends FilteredItemsTable {

    @RDSObjectFactory.Name("level-dependent-items")
    public static class Factory implements RDSObjectFactory {

        @Override
        public RDSObject createInstance(ConfigurationSection config) {

            return new LevelDependantLootTable(config, config.getInt("level", 1));
        }
    }

    private int lowerDiff;
    private int upperDiff;

    public LevelDependantLootTable(ConfigurationSection config, int level) {

        super(config, level - config.getInt("lower-diff", 0), level + config.getInt("upper-diff", 0));
        this.lowerDiff = config.getInt("lower-diff", 0);
        this.upperDiff = config.getInt("upper-diff", 0);
    }

    public void setLevel(int level) {

        clearContents();
        this.minItemLevel = level - lowerDiff;
        this.maxItemLevel = level - upperDiff;
        load(args);
    }
}