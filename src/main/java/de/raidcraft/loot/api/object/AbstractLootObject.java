package de.raidcraft.loot.api.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.tables.TLootPlayer;
import io.ebean.EbeanServer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.*;

@Data
public abstract class AbstractLootObject implements LootObject {

    @Setter(AccessLevel.PROTECTED)
    private int id = -1;
    private RDSTable lootTable;
    private Location hostLocation;
    private long created;
    private boolean enabled = true;
    private int cooldown = -1;
    private boolean publicLootObject = false;
    private boolean infinite = false;

    @Override
    public void assignLootTable(RDSTable lootTable) {

        this.lootTable = lootTable;
    }

    @Override
    public boolean canLoot(UUID player) {
        if (isInfinite()) return !isOnCooldown(player);
        if (isPublicLootObject()) return !hasLooted();
        return !hasLooted(player) && !isOnCooldown(player);
    }


    @Override
    public boolean isOnCooldown(UUID player) {
        if (cooldown < 0) return false;

        if (isPublicLootObject()) {
            return getLastLooter().map(TLootPlayer::getCreated)
                    .map(this::getCooldownEndTime)
                    .map(timestamp -> timestamp.after(new Timestamp(System.currentTimeMillis())))
                    .orElse(false);
        }

        return RaidCraft.getDatabase(LootPlugin.class).find(TLootPlayer.class)
                .where().eq("player_id", player)
                .orderBy("created desc").findList()
                .stream().findFirst()
                .map(TLootPlayer::getCreated)
                .map(this::getCooldownEndTime)
                .map(timestamp -> timestamp.after(new Timestamp(System.currentTimeMillis())))
                .orElse(false);
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
    public boolean hasLooted() {

        return RaidCraft.getDatabase(LootPlugin.class).find(TLootPlayer.class).where()
                .eq("loot_object_id", getId())
                .findList()
                .size() > 0;
    }

    @Override
    public List<ItemStack> loot(UUID player) {

        List<ItemStack> loot = new ArrayList<>();
        // player not yet looted
        if (canLoot(player)) {
            getLootTable().loot().stream()
                    .filter(object -> object instanceof ItemLootObject)
                    .forEach(object -> loot.add(((ItemLootObject) object).getItemStack()));

            // remember loot
            EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
            TLootPlayer lootPlayer = new TLootPlayer();
            lootPlayer.setPlayerId(player);
            lootPlayer.setLootObject(database.find(TLootObject.class, getId()));
            database.save(lootPlayer);
        }
        return loot;
    }

    private Optional<TLootPlayer> getLastLooter() {

        return RaidCraft.getDatabase(LootPlugin.class).find(TLootPlayer.class)
                .orderBy("created desc").findList()
                .stream().findFirst();
    }

    private Timestamp getCooldownEndTime(Timestamp timestamp) {

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(timestamp.getTime());
        calendar.add(Calendar.SECOND, getCooldown());

        return new Timestamp(calendar.getTime().getTime());
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
