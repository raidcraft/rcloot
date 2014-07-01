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

import javax.annotation.Nullable;

/**
 * @author Philip Urban
 */
public class ChestHost implements LootHost {

    @Override
    public Material getMaterial() {

        return Material.CHEST;
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

        return inventory.getType() == InventoryType.CHEST && inventory.getName().toLowerCase().contains("chest");
    }

    @Override
    @Nullable
    public Block getBlock(Inventory inventory) {

        if (inventory.getHolder() instanceof BlockState) {
            return ((BlockState) inventory.getHolder()).getBlock();
        } else if (inventory.getHolder() instanceof DoubleChest) {
            return ((DoubleChest) inventory.getHolder()).getLocation().getBlock();
        }
        return null;
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
