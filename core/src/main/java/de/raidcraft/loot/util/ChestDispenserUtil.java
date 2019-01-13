package de.raidcraft.loot.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

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

        Block otherBlock;

        otherBlock = block.getRelative(1, 0, 0);
        if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
            return otherBlock;
        }
        otherBlock = block.getRelative(-1, 0, 0);
        if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
            return otherBlock;
        }
        otherBlock = block.getRelative(0, 0, 1);
        if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
            return otherBlock;
        }
        otherBlock = block.getRelative(0, 0, -1);
        if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
            return otherBlock;
        }

        if (allDirections) {
            otherBlock = block.getRelative(1, 0, 1);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(1, 0, -1);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(-1, 0, 1);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(-1, 0, -1);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(2, 0, 0);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(-2, 0, 0);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(0, 0, 2);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

            otherBlock = block.getRelative(0, 0, -2);
            if (otherBlock.getType() == Material.CHEST || otherBlock.getType() == Material.TRAPPED_CHEST) {
                return otherBlock;
            }

        }

        return null;
    }

    public static void pasteDoublechest(Player player, Block block) {

        block.setType(Material.CHEST);

        BlockFace face = FacingUtil.yawToFace(player.getLocation().getYaw());

        if (face == BlockFace.NORTH) {
            block.getRelative(-1, 0, 0).setType(Material.CHEST);
        }
        if (face == BlockFace.EAST) {
            block.getRelative(0, 0, -1).setType(Material.CHEST);
        }
        if (face == BlockFace.SOUTH) {
            block.getRelative(1, 0, 0).setType(Material.CHEST);
        }
        if (face == BlockFace.WEST) {
            block.getRelative(0, 0, 1).setType(Material.CHEST);
        }
    }


}
