package de.raidcraft.loot.object;

import de.raidcraft.componentutils.database.Database;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import org.bukkit.block.Block;
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
    private Block host;
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
    public Block getHost() {
        return host;
    }

    @Override
    public void setHost(Block block) {
        this.host = block;
    }

    @Override
    public List<ItemStack> loot(String player) {
        List<ItemStack> loot = new ArrayList<>();
        
        // player not yet looted
        if(id != 0 && !Database.getTable(LootPlayersTable.class).hasLooted(player, id)) {
            for(LootTableEntry entry : getLootTable().loot()) {
                loot.add(entry.getItem());
            }

            // remember loot
            Database.getTable(LootPlayersTable.class).addEntry(player, id, System.currentTimeMillis());
        }
        return loot;
    }
}
