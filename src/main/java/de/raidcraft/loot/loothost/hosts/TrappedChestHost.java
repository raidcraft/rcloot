package de.raidcraft.loot.loothost.hosts;

import de.raidcraft.loot.loothost.LootHost;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Philip Urban
 */
public class TrappedChestHost implements LootHost {

    @Override
    public Material getMaterial() {

        return Material.TRAPPED_CHEST;
    }

    @Override
    public ItemStack[] getContents(Block block) {

        ItemStack[] items;
        items = ((Chest) block.getState()).getInventory().getContents();
        return items;
    }

    @Override
    public void clearContent(Block block) {

        ((Chest) block.getState()).getInventory().clear();
    }

    @Override
    public boolean validateInventory(Inventory inventory) {

        if(inventory.getType() == InventoryType.CHEST && inventory.getName().toLowerCase().contains("chest")) {
            return true;
        }
        return false;
    }

    @Override
    public Block getBlock(Inventory inventory) {

        Block block;

        if (inventory.getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
            block = doubleChest.getLocation().getBlock();
        } else {
            block = ((BlockState) inventory.getHolder()).getBlock();
        }

        return block;
    }

    @Override
    public boolean canBeOpened() {

        return true;
    }

    @Override
    public boolean halfTreasureChance(Inventory inventory) {

        if (!(inventory.getHolder() instanceof DoubleChest)) {
            Block block = ((BlockState) inventory.getHolder()).getBlock();
            if (block.getType() == Material.CHEST) {
                return true;
            }
        }
        return false;
    }
}
