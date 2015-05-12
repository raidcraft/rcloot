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

    public void setId(int id);

    public int getId();

    public void setCreator(UUID player);

    public UUID getCreator();

    public void setCreated(long created);

    public long getCreated();

    public void setEnabled(boolean enabled);

    public boolean isEnabled();

    public RDSTable getLootTable();

    public void assignLootTable(RDSTable lootTable);

    public Location getHostLocation();

    public void setHostLocation(Location block);

    public Collection<RDSObject> loot(UUID player);
}
