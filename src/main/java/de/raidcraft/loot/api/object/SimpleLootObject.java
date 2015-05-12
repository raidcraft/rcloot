package de.raidcraft.loot.api.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 11.10.12 - 20:16
 * Description:
 */
public class SimpleLootObject implements LootObject {

    @Setter
    @Getter
    private int id = 0;
    @Getter
    private RDSTable lootTable;
    private Location hostLocation;
    @Setter
    @Getter
    private UUID creator;
    private long creationDate;
    @Setter
    @Getter
    private boolean enabled;


    @Override
    public void setCreated(long created) {

        this.creationDate = created;
    }

    @Override
    public long getCreated() {

        return creationDate;
    }


    @Override
    public void assignLootTable(RDSTable lootTable) {

        this.lootTable = lootTable;
    }

    @Override
    public Location getHostLocation() {

        return hostLocation;
    }

    @Override
    public void setHostLocation(Location block) {

        this.hostLocation = block;
    }

    @Override
    public Collection<RDSObject> loot(UUID player) {

        // player not yet looted
        if (id != 0 && !RaidCraft.getTable(LootPlayersTable.class).hasLooted(player, id)) {
            RaidCraft.getTable(LootPlayersTable.class).addEntry(player, id, System.currentTimeMillis() / 1000);
            return getLootTable().getResult();
            // remember loot
        }
        return new ArrayList<>();
    }
}
