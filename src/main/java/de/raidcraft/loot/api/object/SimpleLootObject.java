package de.raidcraft.loot.api.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
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
    private LootTable lootTable;
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
    public void assignLootTable(LootTable lootTable) {

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
    public List<ItemStack> loot(UUID player) {

        List<ItemStack> loot = new ArrayList<>();
        // player not yet looted
        if (id != 0 && !RaidCraft.getTable(LootPlayersTable.class).hasLooted(player, id)) {
            for (LootTableEntry entry : getLootTable().loot()) {
                loot.add(entry.getItem().clone());
            }

            // remember loot
            RaidCraft.getTable(LootPlayersTable.class)
                    .addEntry(player, id, System.currentTimeMillis() / 1000);
        }
        return loot;
    }
}
