package de.raidcraft.loot;

import de.faldoria.loot.api.LootObject;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.loot.tables.TLootObject;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.util.LocationUtil;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Philip Urban
 */
@Data
public class LootObjectManager implements Component {

    private final LootPlugin plugin;

    public LootObjectManager(LootPlugin plugin) {
        this.plugin = plugin;
        RaidCraft.registerComponent(LootObjectManager.class, this);
    }

    public void deleteLootObject(LootObject object) {
        if (object == null) return;
        object.delete();
    }

    /**
     * Tries to get a loot-object at the blocks location.
     * Will also look for connected chests and their loot-objects.
     *
     * @param block to get loot-object for
     * @return loot-object or empty optional
     */
    public Optional<LootObject> getLootObject(Block block) {
        if (block == null) return Optional.empty();
        return getLootObject(block.getLocation());
    }

    /**
     * Tries to get a loot-object at the given location.
     * Will also look for connected chests and their loot-objects.
     *
     * @param location to get loot-object for.
     * @return valid loot-object or empty optional
     */
    public Optional<LootObject> getLootObject(Location location) {

        if (location == null) return Optional.empty();

        TLootObject object = TLootObject.find.byLocation(location);

        if (object != null) return Optional.ofNullable(getPlugin().getLootFactory().createLootObject(object));

        Block otherLocation = ChestDispenserUtil.getOtherChestBlock(location.getBlock());
        if (otherLocation != null) {
            object = TLootObject.find.byLocation(otherLocation.getLocation());
            return Optional.ofNullable(getPlugin().getLootFactory().createLootObject(object));
        }

        return Optional.empty();
    }

    /**
     * Tries to find a loot-object with the given id in the database.
     *
     * @param id of the loot-object
     * @return empty optional if id was not found
     */
    public Optional<LootObject> getLootObject(int id) {

        return Optional.ofNullable(getPlugin().getLootFactory().createLootObject(TLootObject.find.byId(id)));
    }

    /**
     * Tries to parse the loot-object id from the inventories name
     * and returns a registered loot-object or an empty optional.
     *
     * @param inventory to get loot-object for
     * @return loot-object if inventory belongs to a loot-object
     */
    public Optional<LootObject> getLootObject(Inventory inventory) {
        if (inventory == null) return Optional.empty();
        LootObject lootObject = getPlugin().getLootFactory().createLootObject(TLootObject.find.byInventory(inventory));
        if (lootObject == null && inventory.getLocation() != null) {
            return getLootObject(inventory.getLocation());
        }
        return Optional.ofNullable(lootObject);
    }

    public boolean isLootObject(Location location) {
        return getLootObject(location).isPresent();
    }

    public void reload() {

    }

    /**
     * Gets all Loot-Objects in the given radius.
     *
     * @param location center
     * @param radius to search
     * @return list of valid loot-objects
     */
    public Collection<LootObject> getNearbyLootObjects(Location location, int radius) {
        return LocationUtil.getNearbyBlocks(location, radius).stream()
                .map(this::getLootObject)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Respawns all destroyed {@link LootObject} if their cooldown is expired or ignored.
     *
     * @param ignoreCooldown if true will respawn all destroyed loot-objects
     * @return number of respawned objects
     */
    public long respawnDestroyedLootObjects(boolean ignoreCooldown) {
        return getPlugin().getRcDatabase().find(TLootObject.class)
                .where().eq("destroyable", true)
                .and().isNotNull("destroyed")
                .findList()
                .stream().map(tLootObject -> getPlugin().getLootFactory().createLootObject(tLootObject))
                .filter(Objects::nonNull)
                .map(object -> object.respawn(ignoreCooldown))
                .filter(aBoolean -> aBoolean)
                .count();
    }

    public Collection<LootObject> getLootObjects() {

        return getPlugin().getRcDatabase().find(TLootObject.class)
                .where().eq("enabled", true)
                .and().isNotNull("destroyed")
                .findList()
                .stream().map(tLootObject -> getPlugin().getLootFactory().createLootObject(tLootObject))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
