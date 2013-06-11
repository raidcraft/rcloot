package de.raidcraft.loot.editor.items;

import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:08
 * Description:
 */
public class TreasureLevel5Item extends SimpleEditorItem {

    private final static Material MATERIAL = Material.WOOL;
    private final static short DATA = 14; // red
    private final static int REWARD_LEVEL = 5;

    @Override
    public ItemStack getItem() {

        return new ItemStack(MATERIAL, 1, DATA);
    }

    @Override
    public void actionRightClick(PlayerInteractEvent event) {

        Block newChestBlock = event.getClickedBlock().getRelative(0, 1, 0);
        if (ChestDispenserUtil.getOtherChestBlock(newChestBlock, true) != null) {
            LootChat.occupiedByOtherChest(event.getPlayer());
            return;
        }

        if (event.getPlayer().isSneaking()) {
            ChestDispenserUtil.pasteDoublechest(event.getPlayer(), newChestBlock);
        } else {
            newChestBlock.setType(Material.CHEST);
        }

        // create treasure loot object
        lootFactory.createTreasureLootObject(event.getPlayer().getName(), newChestBlock, REWARD_LEVEL, true);
    }

    @Override
    public void actionLeftClick(PlayerInteractEvent event) {

        if (!ChestDispenserUtil.isLootableBlock(event.getClickedBlock())) {
            return;
        }

        // create treasure loot object
        lootFactory.createTreasureLootObject(event.getPlayer().getName(), event.getClickedBlock(), REWARD_LEVEL, true);
    }

}
