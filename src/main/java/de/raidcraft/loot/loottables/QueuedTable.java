package de.raidcraft.loot.loottables;

import de.raidcraft.api.random.Loadable;
import de.raidcraft.api.random.RDSTable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

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
            ConfigurationSection args = config.getConfigurationSection("args");
            ((Loadable) table).load(args == null ? new MemoryConfiguration() : args);
        }
    }
}
