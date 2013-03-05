package de.raidcraft.loot.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 20:16
 * Description:
 */
public class SimpleLootObject implements LootObject {

    private int id = 0;
    private LootTable lootTable;
    private Location hostLocation;
    private String creator;
    private long creationDate;
    private boolean enabled;

    @Override
    public void setId(int id) {

        this.id = id;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public void setCreator(String creator) {

        this.creator = creator;
    }

    @Override
    public String getCreator() {

        return creator;
    }

    @Override
    public void setCreated(long created) {

        this.creationDate = created;
    }

    @Override
    public long getCreated() {

        return creationDate;
    }

    @Override
    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {

        return enabled;
    }

    @Override
    public LootTable getLootTable() {

        return lootTable;
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
    public List<ItemStack> loot(String player) {

        List<ItemStack> loot = new ArrayList<>();

        // player not yet looted
        if (id != 0 && !RaidCraft.getTable(LootPlayersTable.class).hasLooted(player, id)) {
            for (LootTableEntry entry : getLootTable().loot()) {
                loot.add(entry.getItem().clone());
            }

            // remember loot
            RaidCraft.getTable(LootPlayersTable.class).addEntry(player, id, System.currentTimeMillis() / 1000);
        }
        return loot;
    }
}
