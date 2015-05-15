package de.raidcraft.loot.api.object;

import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import org.bukkit.Location;

import java.util.Collection;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:27
 * Description:
 */
public interface LootObject {

    void setId(int id);

    int getId();

    void setCreator(UUID player);

    UUID getCreator();

    void setCreated(long created);

    long getCreated();

    void setEnabled(boolean enabled);

    boolean isEnabled();

    RDSTable getLootTable();

    void assignLootTable(RDSTable lootTable);

    Location getHostLocation();

    void setHostLocation(Location block);

    Collection<RDSObject> loot(UUID player);
}
