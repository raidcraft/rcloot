package de.raidcraft.loot.editor.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.object.LootObjectStorage;
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

        LootObjectStorage storage = RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage();
        LootFactory lootFactory = RaidCraft.getComponent(LootPlugin.class).getLootFactory();
        LootObject existingLootObject = storage.getLootObject(event.getClickedBlock().getLocation());
        if (existingLootObject == null) {
            return;
        }
        lootFactory.deleteLootObject(existingLootObject, true);
        LootChat.success(event.getPlayer(), "Das Loot Objekt wurde erfolgreich gelöscht!");

        if (destroy) {
            Block otherChestBlock = ChestDispenserUtil.getOtherChestBlock(existingLootObject.getHostLocation().getBlock());
            if (otherChestBlock != null) {
                ((Chest) otherChestBlock.getState()).getInventory().setContents(new ItemStack[]{});
                otherChestBlock.setType(Material.AIR);
            } else {
                if (existingLootObject.getHostLocation().getBlock().getState() instanceof Chest) {
                    ((Chest) existingLootObject.getHostLocation().getBlock().getState()).getInventory().setContents(new ItemStack[]{});
                }
                if (existingLootObject.getHostLocation().getBlock().getState() instanceof Dispenser) {
                    ((Dispenser) existingLootObject.getHostLocation().getBlock().getState()).getInventory().setContents(new ItemStack[]{});
                }
            }
            existingLootObject.getHostLocation().getBlock().setType(Material.AIR);
        }
    }
}
