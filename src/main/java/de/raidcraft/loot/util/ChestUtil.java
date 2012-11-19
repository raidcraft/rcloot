package de.raidcraft.loot.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:40
 * Description:
 */
public class ChestUtil {
    
    public static Block getOtherChestBlock(Block block) {
        if(block.getType() != Material.CHEST) {
            return null;
        }

        if (block.getRelative(1, 0, 0).getType() == Material.CHEST)
            return block.getRelative(1, 0, 0);
        if (block.getRelative(-1, 0, 0).getType() == Material.CHEST)
            return block.getRelative(-1, 0, 0);
        if (block.getRelative(0, 0, 1).getType() == Material.CHEST)
            return block.getRelative(0, 0, 1);
        if (block.getRelative(0, 0, -1).getType() == Material.CHEST)
            return block.getRelative(0, 0, -1);

        return null;
    }

}
