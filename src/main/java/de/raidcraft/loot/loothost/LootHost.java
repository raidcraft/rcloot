package de.raidcraft.loot.loothost;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Philip Urban
 */
public interface LootHost {

    public Material getMaterial();

    public ItemStack[] getContents(Block block);

    public void clearContent(Block block);

    public boolean validateInventory(Inventory inventory);

    public Block getBlock(Inventory inventory);

    public boolean canBeOpened();

    public boolean halfTreasureChance(Inventory inventory);
}
