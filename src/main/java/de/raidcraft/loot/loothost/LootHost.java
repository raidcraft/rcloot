package de.raidcraft.loot.loothost;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Philip Urban
 */
public interface LootHost {

    Material getMaterial();

    ItemStack[] getContents(Block block);

    void clearContent(Block block);

    boolean validateInventory(Inventory inventory);

    Block getBlock(Inventory inventory);

    boolean canBeOpened();

    boolean halfTreasureChance(Inventory inventory);
}
