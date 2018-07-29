package de.raidcraft.loot.hotbar;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootTableManager;
import de.raidcraft.loot.api.table.LootTable;
import fr.zcraft.zlib.components.gui.ExplorerGui;
import fr.zcraft.zlib.components.gui.GuiAction;
import fr.zcraft.zlib.tools.items.ItemStackBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LootTableListUi extends ExplorerGui<LootTable> {

    @Getter
    private final LootAdminToolbar toolbar;
    @Getter
    private final LootTableManager lootTableManager;

    public LootTableListUi(LootAdminToolbar toolbar) {
        this.toolbar = toolbar;
        this.lootTableManager = RaidCraft.getComponent(LootTableManager.class);
    }

    @Override
    protected void onUpdate() {
        setTitle("Verfügbare Loot-Tabellen");
        setMode(Mode.READONLY);

        setData(getLootTableManager().getTables().toArray(new LootTable[0]));

        action("clear-loottable", getSize() - 4, Material.PAPER, "Loot-Tabelle entfernen.");
        action("close", getSize() - 5, Material.BARRIER, "Menu schließen.");
        setKeepHorizontalScrollingSpace(true);
    }

    @Override
    protected ItemStack getViewItem(LootTable table) {
        return new ItemStackBuilder(Material.PAPER)
                .title(getName(table))
                .lore(table.toString())
                .item();
    }

    @Override
    protected void onRightClick(LootTable table) {
        getToolbar().setLootTable(table);
        getToolbar().setLootTableActive(true);
        getPlayer().sendMessage(ChatColor.GREEN + "Loot-Tabelle " + ChatColor.BLUE + getName(table) + ChatColor.GREEN + " ausgewählt.");
        close();
    }

    private String getName(LootTable table) {
        return ChatColor.BLUE + getLootTableManager().getAlias(table.getId()) + ChatColor.GRAY + " (ID: " + ChatColor.GOLD + table.getId() + ChatColor.GRAY + ")";
    }

    @GuiAction("clear-loottable")
    private void clear_table() {
        getToolbar().setLootTable(null);
    }

    @GuiAction("close")
    private void close_gui() {
        close();
    }
}
