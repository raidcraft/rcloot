package de.raidcraft.loot.util.editormode;

import de.raidcraft.loot.util.editormode.items.DeleteLootObjectItem;
import de.raidcraft.loot.util.editormode.items.TreasureLevel1Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 19.11.12 - 20:50
 * Description:
 */
public class EditorModeFactory {

    public final static EditorModeFactory INSTANCE = new EditorModeFactory();

    private Map<ItemStack, EditorItem> editorItems = new HashMap<>();

    public EditorModeFactory() {

        // register all editor items
        addEditorBlock(new DeleteLootObjectItem());
        addEditorBlock(new TreasureLevel1Item());

    }

    public void addEditorBlock(EditorItem editorItem) {
        editorItems.put(editorItem.getItem(), editorItem);
    }

    public boolean isEditorBlock(ItemStack itemStack) {
        for(Map.Entry<ItemStack, EditorItem> entry : editorItems.entrySet()) {
            if(entry.getKey().getType() == itemStack.getType()
                    && entry.getKey().getDurability() == itemStack.getDurability()) {
                return true;
            }
        }
        return false;
    }
    
    public EditorItem getEditorItem(ItemStack itemStack) {
        for(Map.Entry<ItemStack, EditorItem> entry : editorItems.entrySet()) {
            if(entry.getKey().getType() == itemStack.getType()
                    && entry.getKey().getDurability() == itemStack.getDurability()) {
                return entry.getValue();
            }
        }
        return null;
    }

}
