package de.raidcraft.loot.lootobjects;

import de.faldoria.loot.api.LootObject;
import de.faldoria.loot.api.events.RCLootObjectSpawnEvent;
import de.faldoria.loot.api.events.RCLootObjectSpawnedEvent;
import de.faldoria.loot.api.events.RCPlayerLootEvent;
import de.faldoria.loot.api.events.RCPlayerLootedEvent;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.Skull;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.api.random.objects.ItemLootObject;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.tables.TLootPlayer;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.util.TimeUtil;
import io.ebean.EbeanServer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {"id", "hostLocation", "otherHostLocation"})
public abstract class AbstractLootObject implements LootObject {

    @Setter(AccessLevel.PROTECTED)
    private long id = -1;
    private RDSTable lootTable;
    private Location hostLocation;
    private Location otherHostLocation;
    private Material material;
    private BlockData blockData;
    private String extraData;
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

    public Optional<BlockData> getBlockData() {
        return Optional.ofNullable(this.blockData);
    }

    public Optional<String> getExtraData() {
        return Optional.ofNullable(this.extraData);
    }

    @Override
    public void assignLootTable(RDSTable lootTable) {

        this.lootTable = lootTable;
    }

    @Override
    public boolean canLoot(UUID player) {
        if (isInfinite()) return !isOnCooldown(player);
        if (isPublicLootObject()) return !hasLooted();
        return (getCooldown() < 1 && !hasLooted(player)) || (getCooldown() > 0 && !isOnCooldown(player));
    }


    @Override
    public boolean isOnCooldown(UUID player) {
        if (cooldown < 0) return false;

        if (isPublicLootObject()) {
            return getLastLooter().map(TLootPlayer::getCreated)
                    .map(this::getCooldownEndTime)
                    .map(timestamp -> timestamp.isAfter(Instant.now()))
                    .orElse(false);
        }

        return RaidCraft.getDatabase(LootPlugin.class).find(TLootPlayer.class)
                .where().eq("player_id", player)
                .and().eq("loot_object_id", getId())
                .orderBy("created desc").findList()
                .stream().findFirst()
                .map(TLootPlayer::getCreated)
                .map(this::getCooldownEndTime)
                .map(timestamp -> timestamp.isAfter(Instant.now()))
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
    public ItemStack[] loot(UUID player) {

        ItemStack[] itemStacks = new ItemStack[0];

        // player not yet looted
        if (canLoot(player)) {
            @Nullable Player bukkitPlayer = Bukkit.getPlayer(player);
            Collection<RDSObject> result = getLootTable().loot(bukkitPlayer);
            if (bukkitPlayer != null) {
                RCPlayerLootEvent event = new RCPlayerLootEvent(bukkitPlayer, this, result.toArray(new RDSObject[0]));
                RaidCraft.callEvent(event);
                if (event.isCancelled()) return new ItemStack[0];
            }

            itemStacks = result.stream()
                    .filter(object -> object instanceof ItemLootObject)
                    .map(object -> ((ItemLootObject) object).getItemStack())
                    .toArray(ItemStack[]::new);

            // remember loot
            EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
            TLootPlayer lootPlayer = new TLootPlayer();
            lootPlayer.setPlayerId(player);
            lootPlayer.setLootObject(database.find(TLootObject.class, getId()));
            database.save(lootPlayer);

            if (bukkitPlayer != null) {
                RCPlayerLootedEvent event = new RCPlayerLootedEvent(bukkitPlayer, this, itemStacks);
                RaidCraft.callEvent(event);
            }
        }

        return itemStacks;
    }

    @Override
    public void destroy(boolean dropLoot) {
        if (!isDestroyable()) return;

        if (getHostLocation().getBlock().getType() != Material.AIR) {
            setMaterial(getHostLocation().getBlock().getType());
            if (!getBlockData().isPresent()) {
                setBlockData(getHostLocation().getBlock().getBlockData());
            }
            if (!getExtraData().isPresent()) {
                    Skull.serializeSkull(getHostLocation().getBlock()).ifPresent(this::setExtraData);
            }
        }
        getHostLocation().getBlock().setType(Material.AIR);
        if (isDoubleChest()) getOtherHostLocation().getBlock().setType(Material.AIR);
        setDestroyed(Instant.now());
        save();

        if (dropLoot) {
            for (ItemStack itemStack : loot(LootFactory.ANY)) {
                getHostLocation().getWorld().dropItemNaturally(getHostLocation(), itemStack);
            }
        }
    }

    @Override
    public void destroy() {
        this.destroy(true);
    }

    @Override
    public boolean respawn(boolean force) {

        if (!isDestroyable() || getDestroyed() == null) return false;

        RCLootObjectSpawnEvent event = new RCLootObjectSpawnEvent(this);
        event.setForce(force);
        RaidCraft.callEvent(event);
        if (event.isCancelled()) return false;
        force = event.isForce();

        if (!force && (isPublicLootObject() && getCooldown() <= 0)) return false;

        if (getMaterial() == null) {
            RaidCraft.LOGGER.warning("Cannot respawn Loot-Object (ID: " + getId() + "): invalid or null material!");
            return false;
        }
        if (force || getCooldownEndTime(getDestroyed()).isBefore(Instant.now())) {
            getHostLocation().getBlock().setType(getMaterial());
            getBlockData().ifPresent(data -> getHostLocation().getBlock().setBlockData(data));
            getExtraData().ifPresent(extraData -> Skull.applySerializedSkull(getHostLocation().getBlock(), extraData));
            setDestroyed(null);
            save();
            RaidCraft.callEvent(new RCLootObjectSpawnedEvent(this));
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
                .where().eq("loot_object_id", getId())
                .orderBy("created desc").findList()
                .stream().findFirst();
    }

    /**
     * Gets the cooldown end time of the loot-object based on the provided last loot-time.
     *
     * @param timestamp the object was last looted
     * @return last-looted plus cooldown time
     */
    private Instant getCooldownEndTime(Timestamp timestamp) {

        return getCooldownEndTime(timestamp.toInstant());
    }

    /**
     * Gets the cooldown end time of the loot-object based on the provided last loot-time.
     *
     * @param instant the object was last looted
     * @return last-looted plus cooldown time
     */
    private Instant getCooldownEndTime(Instant instant) {
        return instant.plusSeconds(getCooldown());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD + "LootObject[").append(ChatColor.AQUA).append(getMaterial().name()).append(ChatColor.GOLD + "]")
                .append(" (" + ChatColor.BLUE + "ID: ").append(getId()).append(ChatColor.GOLD + ")")
                .append("\n");
        getLootTable().getId().ifPresent(id -> sb.append(ChatColor.DARK_GRAY).append("Loot-Table: ").append(id).append("\n"));
        sb.append(ChatColor.DARK_GRAY).append("Created: ").append(new Timestamp(created).toString()).append("\n");
        sb.append(ChatColor.DARK_GRAY).append("Enabled: ").append(isEnabled()).append("\n");
        sb.append(ChatColor.DARK_GRAY).append("Public: ").append(isPublicLootObject()).append("\n");
        sb.append(ChatColor.DARK_GRAY).append("Infinite: ").append(isInfinite()).append("\n");
        sb.append(ChatColor.DARK_GRAY).append("Destroyable: ").append(isDestroyable()).append("\n");
        sb.append(ChatColor.DARK_GRAY).append("Cooldown: ").append(TimeUtil.getFormattedTime(getCooldown())).append("\n");

        return sb.toString();
    }
}
