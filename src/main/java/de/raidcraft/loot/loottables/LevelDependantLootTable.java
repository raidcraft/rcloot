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

    private final ConfigurationSection config;

    public LevelDependantLootTable(ConfigurationSection config, int level) {

        super(config, level - config.getInt("lower-diff", 0), level + config.getInt("upper-diff", 0));
        this.config = config;
    }

    public void setLevel(int level) {

        clearContents();
        config.set("min-level", level - config.getInt("lower-diff", 0));
        config.set("max-level", level + config.getInt("upper-diff", 0));
        load(config);
    }
}