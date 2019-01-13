package de.faldoria.loot.toolbar;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.loot.LootTableManager;
import fr.zcraft.zlib.components.gui.ExplorerGui;
import fr.zcraft.zlib.components.gui.GuiAction;
import fr.zcraft.zlib.tools.items.ItemStackBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LootTableListUi extends ExplorerGui<RDSTable> {

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

        setData(getLootTableManager().getTables().toArray(new RDSTable[0]));

        action("clear-loottable", getSize() - 4, Material.PAPER, "Loot-Tabelle entfernen.");
        action("close", getSize() - 5, Material.BARRIER, "Menu schließen.");
        setKeepHorizontalScrollingSpace(true);
    }

    @Override
    protected ItemStack getViewItem(RDSTable table) {
        return RDS.getLootTableMeta(table.getId().orElse(null))
            .map(meta -> new ItemStackBuilder(meta.getIcon().orElse(Material.PAPER))
                .title(meta.getName().orElse(getName(table)))
                .lore(ChatColor.GRAY + meta.getDescription().orElse(""),
                        ChatColor.GRAY + "Rechtsklick: wählt die Loot-Tabelle aus.")
                .item())
            .orElseGet(() -> new ItemStackBuilder(Material.PAPER)
                        .title(getName(table))
                        .lore(ChatColor.GRAY + "Rechtsklick: wählt die Loot-Tabelle aus.")
                        .item());
    }

    @Override
    protected void onRightClick(RDSTable table) {
        getToolbar().setLootTable(table);
        getToolbar().setLootTableActive(true);
        getPlayer().sendMessage(ChatColor.GREEN + "Loot-Tabelle " + getName(table) + ChatColor.GREEN + " ausgewählt.");
        close();
    }

    private String getName(RDSTable table) {
        return ChatColor.BLUE + table.getId().orElse("N/A");
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
