package de.raidcraft.loot.api.object;

import de.raidcraft.loot.api.table.LootTable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Author: Philip
 * Date: 11.10.12 - 19:27
 * Description:
 */
public interface LootObject {

    public void setId(int id);

    public int getId();

    public void setCreator(String player);

    public String getCreator();

    public void setCreated(long created);

    public long getCreated();

    public void setEnabled(boolean enabled);

    public boolean isEnabled();

    public LootTable getLootTable();

    public void assignLootTable(LootTable lootTable);

    public Location getHostLocation();

    public void setHostLocation(Location block);

    public List<ItemStack> loot(String player);
}
