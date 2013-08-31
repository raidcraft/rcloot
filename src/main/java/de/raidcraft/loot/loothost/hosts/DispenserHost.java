package de.raidcraft.loot.loothost.hosts;

import de.raidcraft.loot.loothost.LootHost;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Philip Urban
 */
public class DispenserHost implements LootHost {

    @Override
    public Material getMaterial() {

        return Material.DISPENSER;
    }

    @Override
    public ItemStack[] getContents(Block block) {

        ItemStack[] items;
        items = ((Dispenser) block.getState()).getInventory().getContents();
        return items;
    }

    @Override
    public void clearContent(Block block) {

        ((Dispenser) block.getState()).getInventory().clear();
    }

    @Override
    public boolean validateInventory(Inventory inventory) {

        if(inventory.getType() == InventoryType.DISPENSER && inventory.getName().toLowerCase().contains("dispenser")) {
            return true;
        }
        return false;
    }

    @Override
    public Block getBlock(Inventory inventory) {

        Block block = ((BlockState) inventory.getHolder()).getBlock();
        return block;
    }

    @Override
    public boolean canBeOpened() {

        return false;
    }

    @Override
    public boolean halfTreasureChance(Inventory inventory) {

        return false;
    }
}
