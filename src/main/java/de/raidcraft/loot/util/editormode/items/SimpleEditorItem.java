package de.raidcraft.loot.util.editormode.items;

import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.editormode.EditorItem;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:24
 * Description:
 */
public abstract class SimpleEditorItem implements EditorItem {

    @Override
    public final void run(PlayerInteractEvent event) {

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            actionRightClick(event);
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            actionLeftClick(event);
            return;
        }
        LootChat.warn(event.getPlayer(), "Die Aktion konnte nicht zugeordnet werden!");
    }

    @Override
    abstract public ItemStack getItem();

    @Override
    abstract public void actionRightClick(PlayerInteractEvent event);

    @Override
    abstract public void actionLeftClick(PlayerInteractEvent event);
}
