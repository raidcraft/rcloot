package de.raidcraft.loot.util.editormode.items;

import com.sk89q.worldguard.bukkit.LoggerToChatHandler;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.Utils;
import de.raidcraft.loot.util.editormode.EditorItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:08
 * Description:
 */
public class TreasureLevel1Item extends SimpleEditorItem {

    private final static Material MATERIAL = Material.WOOL;
    private final static short DATA = 8; // light grey
    private final static int REWARD_LEVEL = 1;
    
    @Override
    public ItemStack getItem() {

        return new ItemStack(MATERIAL, 1, DATA);
    }

    @Override
    public void actionRightClick(PlayerInteractEvent event) {

        Block newChestBlock = event.getClickedBlock().getRelative(0, 1, 0);
        if(ChestDispenserUtil.getOtherChestBlock(newChestBlock, true) != null) {
            LootChat.occupiedByOtherChest(event.getPlayer());
            return;
        }

        ChestDispenserUtil.pasteDoublechest(event.getPlayer(), newChestBlock);

        // create treasure loot object
        LootFactory.inst.createTreasureLootObject(event.getPlayer().getName(), newChestBlock, REWARD_LEVEL);
        LootChat.successfullyCreatedLootObject(event.getPlayer());
    }

    @Override
    public void actionLeftClick(PlayerInteractEvent event) {

        if(!ChestDispenserUtil.isChestOrDispenser(event.getClickedBlock())) {
            return;
        }
        
        // create treasure loot object
        LootFactory.inst.createTreasureLootObject(event.getPlayer().getName(), event.getClickedBlock(), REWARD_LEVEL);
        LootChat.successfullyCreatedLootObject(event.getPlayer());
    }

}
