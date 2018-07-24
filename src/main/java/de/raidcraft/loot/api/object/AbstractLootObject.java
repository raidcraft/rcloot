package de.raidcraft.loot.api.object;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.tables.TLootPlayer;
import lombok.Data;
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
@Data
public abstract class AbstractLootObject implements LootObject {

    private final int id;
    private LootTable lootTable;
    private Location hostLocation;
    private long created;
    private boolean enabled = true;
    private int cooldown = -1;
    private boolean publicLootObject = false;
    private boolean infinite = false;

    @Override
    public void assignLootTable(LootTable lootTable) {

        this.lootTable = lootTable;
    }

    @Override
    public boolean canLoot(UUID player) {
        return true;
    }

    @Override
    public boolean hasLooted(UUID player) {

        List<TLootPlayer> players = RaidCraft.getDatabase(LootPlugin.class).find(TLootPlayer.class).where()
                .eq("loot_object_id", getId())
                .eq("player_id", player)
                .findList();

        return players.size() > 0;
    }

    @Override
    public List<ItemStack> loot(UUID player) {

        List<ItemStack> loot = new ArrayList<>();
        // player not yet looted
        if (canLoot(player)) {
            for (LootTableEntry entry : getLootTable().loot()) {
                loot.add(entry.getItem().clone());
            }

            // remember loot
            EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
            TLootPlayer lootPlayer = new TLootPlayer();
            lootPlayer.setPlayerId(player);
            lootPlayer.setLootObject(database.find(TLootObject.class, getId()));
            database.save(lootPlayer);
        }
        return loot;
    }

    @Override
    public String toString() {
        return "AbstractLootObject{" +
                "id=" + id +
                ", lootTable=" + lootTable +
                ", hostLocation=" + hostLocation +
                ", created=" + created +
                ", enabled=" + enabled +
                ", cooldown=" + cooldown +
                ", publicLootObject=" + publicLootObject +
                ", infinite=" + infinite +
                '}';
    }
}
