package de.raidcraft.loot.lootobjects;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.tables.TLootPlayer;
import de.raidcraft.loot.util.ChestDispenserUtil;
import io.ebean.EbeanServer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(of = {"id", "hostLocation", "otherHostLocation"})
public abstract class AbstractLootObject implements LootObject {

    @Setter(AccessLevel.PROTECTED)
    private long id = -1;
    private RDSTable lootTable;
    private Location hostLocation;
    private Location otherHostLocation;
    private Material material;
    private long created;
    private boolean enabled = true;
    private int cooldown = -1;
    private boolean publicLootObject = false;
    private boolean infinite = false;
    private boolean destroyable = false;
    private Instant destroyed = null;

    @Override
    public void setHostLocation(Location hostLocation) {

        Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(hostLocation.getBlock());
        if (otherChestBlock != null) setOtherHostLocation(otherChestBlock.getLocation());
        this.hostLocation = hostLocation;
    }

    @Override
    public boolean isDoubleChest() {
        return getOtherHostLocation() != null;
    }

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

    @Override
    public void destroy(boolean dropLoot) {
        if (!isDestroyable()) return;

        getHostLocation().getBlock().setType(Material.AIR);
        if (isDoubleChest()) getOtherHostLocation().getBlock().setType(Material.AIR);
        setDestroyed(Instant.now());
        save();

        if (dropLoot) {
            List<ItemStack> loot = loot(LootFactory.ANY);
            loot.forEach(itemStack -> getHostLocation().getWorld().dropItemNaturally(getHostLocation(), itemStack));
        }
    }

    @Override
    public void destroy() {
        this.destroy(true);
    }

    @Override
    public boolean respawn(boolean force) {

        if (!isDestroyable() || getDestroyed() == null) return false;
        if (getMaterial() == null) {
            RaidCraft.LOGGER.warning("Cannot respawn Loot-Object (ID: " + getId() + "): invalid or null material!");
            return false;
        }
        if (force || getDestroyed().plusSeconds(getCooldown()).isBefore(Instant.now())) {
            getHostLocation().getBlock().setType(getMaterial());
            setDestroyed(null);
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean respawn() {
        return respawn(false);
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
