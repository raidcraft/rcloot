package de.raidcraft.loot.editor.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.editor.EditorItem;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Author: Philip
 * Date: 19.11.12 - 21:24
 * Description:
 */
public abstract class SimpleEditorItem implements EditorItem {

    protected LootFactory lootFactory = RaidCraft.getComponent(LootPlugin.class).getLootFactory();

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
