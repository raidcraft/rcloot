package de.raidcraft.loot.lootobjects;

import de.raidcraft.api.random.RDSTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:27
 * Description:
 */
public interface LootObject {

    long getId();

    Material getMaterial();

    void setCreated(long created);

    long getCreated();

    void setEnabled(boolean enabled);

    boolean isEnabled();

    RDSTable getLootTable();

    void assignLootTable(RDSTable lootTable);

    Location getHostLocation();

    Location getOtherHostLocation();

    void setHostLocation(Location block);

    boolean canLoot(UUID player);

    boolean isOnCooldown(UUID player);

    boolean hasLooted(UUID player);

    boolean hasLooted();

    List<ItemStack> loot(UUID player);

    int getCooldown();

    /**
     * If the {@link LootObject} is public the cooldown and looted players are shared.
     */
    boolean isPublicLootObject();

    void setCooldown(int cooldown);

    void setPublicLootObject(boolean publicLootObject);

    boolean isInfinite();

    void setInfinite(boolean infinite);

    /**
     * If a {@link LootObject} is destroyable it will be removed after
     * a player looted. Will respawn after the given {@link #getCooldown()}.
     * Destroyable loot-objects are automatically public {@link #isPublicLootObject()} -> true.
     * <br/>
     * If a the cooldown is less or equal to zero the loot-object will not respawn
     * and be deleted from the database after being looted. This is considered a unique loot-object.
     *
     * @return true if the loot-object can be destroyed and respawns.
     */
    boolean isDestroyable();

    void setDestroyable(boolean destroyable);

    /**
     * Gets the last time the loot-object was destroyed.
     * Is null if the loot-object is currently not destroyed.
     *
     * @return null if loot-object is not destroyed or last destroy time
     */
    Instant getDestroyed();

    /**
     * Destroys the loot-object and drops the loot accordingly.
     * Will have no effect if {@link #isDestroyable()} is false.
     *
     * @param dropLoot set to true if you want to drop the loot by calling {@link #loot(UUID)}
     */
    void destroy(boolean dropLoot);

    /**
     * Destroys the loot-object dropping loot.
     */
    void destroy();

    /**
     * Respawns this {@link LootObject} if it {@link #isDestroyable()}
     * and {@link #getDestroyed()} is not null. Will check the cooldown
     * against the destroyed time if not forced to respawn.
     *
     * @param force ignores the respawn cooldown
     * @return true if the loot-object was respawned
     */
    boolean respawn(boolean force);

    /**
     * Respawns this {@link LootObject} checking the cooldown.
     * @return true if the loot-object was respawned
     */
    boolean respawn();

    boolean isDoubleChest();

    void save();

    /**
     * Deletes this {@link LootObject} from the database.
     */
    void delete();
}
