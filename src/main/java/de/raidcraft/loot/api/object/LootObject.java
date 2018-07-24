package de.raidcraft.loot.api.object;

import de.raidcraft.loot.api.table.LootTable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:27
 * Description:
 */
public interface LootObject {

    int getId();

    public void setCreated(long created);

    public long getCreated();

    abstract void setEnabled(boolean enabled);

    abstract boolean isEnabled();

    abstract LootTable getLootTable();

    public void assignLootTable(LootTable lootTable);

    abstract Location getHostLocation();

    public void setHostLocation(Location block);

    boolean canLoot(UUID player);

    boolean hasLooted(UUID player);

    public List<ItemStack> loot(UUID player);

    int getCooldown();

    /**
     * If the {@link LootObject} is public the cooldown and looted players are shared.
     */
    boolean isPublicLootObject();

    void setCooldown(int cooldown);

    void setPublicLootObject(boolean publicLootObject);

    boolean isInfinite();

    void setInfinite(boolean infinite);

    void save();

    void delete();
}
