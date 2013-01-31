package de.raidcraft.loot.util.editormode.items;

import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:34
 * Description:
 */
public class DeleteLootObjectItem extends SimpleEditorItem {

    private final static Material MATERIAL = Material.BUCKET;
    private final static short DATA = 0;

    @Override
    public ItemStack getItem() {

        return new ItemStack(MATERIAL, 1, DATA);
    }

    @Override
    public void actionRightClick(PlayerInteractEvent event) {

        deleteLootObject(event, false);
    }

    @Override
    public void actionLeftClick(PlayerInteractEvent event) {

        deleteLootObject(event, true);
    }

    private void deleteLootObject(PlayerInteractEvent event, boolean destroy) {

        LootObject existingLootObject = LootFactory.inst.getLootObject(event.getClickedBlock());
        if (existingLootObject == null) {
            return;
        }
        LootFactory.inst.deleteLootObject(existingLootObject, true);
        LootChat.success(event.getPlayer(), "Das Loot Objekt wurde erfolgreich gelöscht!");

        if (destroy) {
            Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(existingLootObject.getHost());
            if (otherChestBlock != null) {
                ((Chest) otherChestBlock.getState()).getInventory().setContents(new ItemStack[]{});
                otherChestBlock.setType(Material.AIR);
            } else {
                if (existingLootObject.getHost().getState() instanceof Chest) {
                    ((Chest) existingLootObject.getHost().getState()).getInventory().setContents(new ItemStack[]{});
                }
                if (existingLootObject.getHost().getState() instanceof Dispenser) {
                    ((Dispenser) existingLootObject.getHost().getState()).getInventory().setContents(new ItemStack[]{});
                }
            }
            existingLootObject.getHost().setType(Material.AIR);
        }
    }
}
