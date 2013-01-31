package de.raidcraft.loot.util.editormode;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 20:57
 * Description:
 */
public interface EditorItem {

    public ItemStack getItem();

    public void run(PlayerInteractEvent event);

    public void actionRightClick(PlayerInteractEvent event);

    public void actionLeftClick(PlayerInteractEvent event);

}
