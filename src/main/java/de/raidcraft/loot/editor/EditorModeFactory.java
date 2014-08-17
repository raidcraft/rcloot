package de.raidcraft.loot.editor;

import de.raidcraft.loot.editor.items.DeleteLootObjectItem;
import de.raidcraft.loot.editor.items.TreasureLevel1Item;
import de.raidcraft.loot.editor.items.TreasureLevel2Item;
import de.raidcraft.loot.editor.items.TreasureLevel3Item;
import de.raidcraft.loot.editor.items.TreasureLevel4Item;
import de.raidcraft.loot.editor.items.TreasureLevel5Item;
import de.raidcraft.loot.editor.items.TreasureLevel6Item;
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
        addEditorBlock(new TreasureLevel2Item());
        addEditorBlock(new TreasureLevel3Item());
        addEditorBlock(new TreasureLevel4Item());
        addEditorBlock(new TreasureLevel5Item());
        addEditorBlock(new TreasureLevel6Item());
    }

    public void addEditorBlock(EditorItem editorItem) {

        editorItems.put(editorItem.getItem(), editorItem);
    }

    public boolean isEditorBlock(ItemStack itemStack) {

        for (Map.Entry<ItemStack, EditorItem> entry : editorItems.entrySet()) {
            if (entry.getKey().getType() == itemStack.getType()
                    && entry.getKey().getDurability() == itemStack.getDurability()) {
                return true;
            }
        }
        return false;
    }

    public EditorItem getEditorItem(ItemStack itemStack) {

        for (Map.Entry<ItemStack, EditorItem> entry : editorItems.entrySet()) {
            if (entry.getKey().getType() == itemStack.getType()
                    && entry.getKey().getDurability() == itemStack.getDurability()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
