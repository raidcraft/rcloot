package de.raidcraft.loot.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:40
 * Description:
 */
public class ChestDispenserUtil {

    public static Block getOtherChestBlock(Block block) {
        return getOtherChestBlock(block, false);
    }

    public static Block getOtherChestBlock(Block block, boolean allDirections) {

        if (block.getRelative(1, 0, 0).getType() == Material.CHEST)
            return block.getRelative(1, 0, 0);
        if (block.getRelative(-1, 0, 0).getType() == Material.CHEST)
            return block.getRelative(-1, 0, 0);
        if (block.getRelative(0, 0, 1).getType() == Material.CHEST)
            return block.getRelative(0, 0, 1);
        if (block.getRelative(0, 0, -1).getType() == Material.CHEST)
            return block.getRelative(0, 0, -1);

        if(allDirections) {
            if (block.getRelative(1, 0, 1).getType() == Material.CHEST)
                return block.getRelative(1, 0, 1);
            if (block.getRelative(1, 0, -1).getType() == Material.CHEST)
                return block.getRelative(1, 0, -1);
            if (block.getRelative(-1, 0, 1).getType() == Material.CHEST)
                return block.getRelative(-1, 0, 1);
            if (block.getRelative(-1, 0, -1).getType() == Material.CHEST)
                return block.getRelative(-1, 0, -1);
            if (block.getRelative(2, 0, 0).getType() == Material.CHEST)
                return block.getRelative(2, 0, 0);
            if (block.getRelative(-2, 0, 0).getType() == Material.CHEST)
                return block.getRelative(-2, 0, 0);
            if (block.getRelative(0, 0, 2).getType() == Material.CHEST)
                return block.getRelative(0, 0, 2);
            if (block.getRelative(0, 0, -2).getType() == Material.CHEST)
                return block.getRelative(0, 0, -2);
        }

        return null;
    }
    
    public static boolean isChestOrDispenser(Block block) {
        if (block != null
                && (block.getType() == Material.DISPENSER
                || block.getType() == Material.CHEST)) {
            return true;
        }
        return false;
    }
    
    public static ItemStack[] getItems(Block block) {
        ItemStack[] items = new ItemStack[]{null};

        if (block.getState() instanceof Chest) {
            items = ((Chest) block.getState()).getInventory().getContents();
            ((Chest) block.getState()).getInventory().clear();
        }
        if (block.getState() instanceof Dispenser) {
            items = ((Dispenser) block.getState()).getInventory().getContents();
        }

        return items;
    }

    public static void pasteDoublechest(Player player, Block block) {
        block.setType(Material.CHEST);

        BlockFace face = Utils.yawToFace(player.getLocation().getYaw());

        if(face == BlockFace.NORTH) {
            block.getRelative(-1, 0, 0).setType(Material.CHEST);
        }
        if(face == BlockFace.EAST) {
            block.getRelative(0, 0, -1).setType(Material.CHEST);
        }
        if(face == BlockFace.SOUTH) {
            block.getRelative(1, 0, 0).setType(Material.CHEST);
        }
        if(face == BlockFace.WEST) {
            block.getRelative(0, 0, 1).setType(Material.CHEST);
        }
    }
}
