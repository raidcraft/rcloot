package de.raidcraft.loot.util.editormode.items;

import de.raidcraft.loot.util.editormode.EditorItem;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:08
 * Description:
 */
public class TreasureLevel1 extends SimpleEditorItem {

    private final static Material MATERIAL = Material.WOOL;
    private final static short DATA = 8; // light grey
    
    @Override
    public ItemStack getItem() {

        return new ItemStack(MATERIAL, 1, DATA);
    }

    @Override
    public void actionRightClick(PlayerInteractEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void actionLeftClick(PlayerInteractEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
