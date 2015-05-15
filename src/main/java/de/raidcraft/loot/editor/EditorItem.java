package de.raidcraft.loot.editor;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 20:57
 * Description:
 */
public interface EditorItem {

    ItemStack getItem();

    void run(PlayerInteractEvent event);

    void actionRightClick(PlayerInteractEvent event);

    void actionLeftClick(PlayerInteractEvent event);

}
