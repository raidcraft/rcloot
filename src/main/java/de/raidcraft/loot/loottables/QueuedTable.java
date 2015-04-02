package de.raidcraft.loot.loottables;

import de.raidcraft.api.random.Loadable;
import de.raidcraft.api.random.RDSTable;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class QueuedTable {

    private final RDSTable table;
    private final ConfigurationSection config;

    public QueuedTable(RDSTable table, ConfigurationSection config) {

        this.table = table;
        this.config = config;
    }

    public void load() {

        if (table instanceof Loadable) {
            ((Loadable) table).load(config);
        }
    }
}
