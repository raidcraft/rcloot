package de.raidcraft.loot.hotbar;

import com.google.common.collect.Sets;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.combatbar.api.HotbarName;
import de.raidcraft.combatbar.slots.ActionHotbarSlot;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootTableManager;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.object.LootObjectStorage;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.loothost.LootHost;
import de.raidcraft.loot.loothost.LootHostManager;
import fr.zcraft.zlib.components.gui.Gui;
import fr.zcraft.zlib.tools.items.ItemStackBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

@Data
@HotbarName("loot-admin")
@EqualsAndHashCode(callSuper = true)
public class LootAdminToolbar extends Hotbar {

    private static final int LOOTTABLE_LOOKUP_DISTANCE = 10;
    private static final Set<Material> TRANSPARENT_BLOCKS = Sets.newHashSet(Material.AIR, Material.WATER, Material.LAVA);

    private final LootTableManager lootTableManager = RaidCraft.getComponent(LootTableManager.class);
    private final LootHostManager lootHostManager = RaidCraft.getComponent(LootHostManager.class);
    private final LootObjectStorage lootObjectStorage = RaidCraft.getComponent(LootObjectStorage.class);
    private final LootFactory lootFactory = RaidCraft.getComponent(LootFactory.class);
    private LootTable lootTable = null;
    private boolean lootTableActive = false;

    public LootAdminToolbar(HotbarHolder holder) {
        super(holder);

        // display active loottable
        addHotbarSlot(new ActionHotbarSlot()
                .setItemGetter(() -> new ItemStackBuilder(Material.PAPER)
                        .title(lootTable != null ? ChatColor.GOLD + "Aktive Loot-Tabelle: "
                                + getLootTableAlias()
                                : ChatColor.RED + "Keine Loot-Tabelle ausgewählt!")
                        .lore(ChatColor.GOLD + "Auswählen: " + ChatColor.GRAY + "Loot-Tabelle des Ziels auswählen",
                                ChatColor.GOLD + "Rechtsklick: " + ChatColor.GRAY + "Loot-Tabelle " + ChatColor.GREEN + "aktivieren",
                                ChatColor.GOLD + "Linksklick: " + ChatColor.GRAY + "Loot-Tabelle " + ChatColor.RED + "deaktivieren",
                                ChatColor.GOLD + "Inventar Links Klick: " + ChatColor.GRAY + "Andere Loot-Tabelle auswählen",
                                ChatColor.GOLD + "Inventar Links Klick: " + ChatColor.GRAY + "Aktive Loot-Tabelle bearbeiten")
                        .item())
                .setOnSelect(this::selectTargetAsLootTable)
                .setOnInventoryLeftClick(this::openChooseLootTableMenu)
                .setOnInventoryRightClick(this::openEditLootTableMenu)
                .setOnRightClickInteract(player -> setLootTableActive(true))
                .setOnLeftClickInteract(player -> setLootTableActive(false))
        );
        // create new loottable
        addHotbarSlot(new ActionHotbarSlot(
                        new ItemStackBuilder(Material.BOOK_AND_QUILL)
                                .title(ChatColor.DARK_GREEN, "Neue Loot-Tabelle erstellen")
                                .lore(ChatColor.GOLD + "Rechtsklick: " + ChatColor.GRAY + "Neue Loot-Tabelle aus Kiste",
                                        ChatColor.GOLD + "Inventar Klick: " + ChatColor.GRAY + "Neue Loot-Tabelle")
                                .item()
                ).setOnInteract(this::createNewLootTableFromChest)
                        .setOnInventoryClick(this::openCreateNewLootTableMenu)
        );
        addHotbarSlot(new ActionHotbarSlot(
                new ItemStackBuilder(Material.CHEST)
                        .title(ChatColor.BLUE + "Loot-Tabelle auf Kiste anwenden")
                        .lore(ChatColor.GOLD + "Auswählen: " + ChatColor.GRAY + "Kiste in Loot-Kiste umwandeln",
                                ChatColor.GOLD + "Rechtsklick: " + ChatColor.GRAY + "Neue Loot-Kiste mit aktiver Loot-Tabelle setzen",
                                ChatColor.GOLD + "Linksklick: " + ChatColor.GRAY + "Bestehende Loot-Kiste entfernen")
                        .item())
                .setOnSelect(this::convertChestToLootChest)
                .setOnLeftClickInteract(this::destroyLootChest)
                .setCancelBlockPlacement(true)
                .setOnPlayerPlaceBlock(this::placeLootChest)
        );
    }

    private String getLootTableAlias() {
        return ChatColor.AQUA + getLootTableManager().getAlias(getLootTable().getId()) + ChatColor.GRAY + "(" + getLootTable().getId() + ") "
                + (isLootTableActive() ? ChatColor.GREEN + "AN" : ChatColor.RED + "AUS" + ChatColor.GOLD);
    }

    public boolean isLootTableActive() {
        return lootTableActive && lootTable != null;
    }

    public void setLootTableActive(boolean active) {
        this.lootTableActive = active;
        if (getLootTable() == null) {
            getPlayer().sendMessage(ChatColor.RED + "Keine Loot-Tabelle ausgewählt.");
            this.lootTableActive = false;
        } else {
            getPlayer().sendMessage(ChatColor.GRAY + "Loot-Tabelle: " + getLootTableAlias());
        }
    }

    public void setLootTable(LootTable lootTable) {
        this.lootTable = lootTable;
        if (this.lootTable == null) {
            getPlayer().sendMessage(ChatColor.GOLD + "Aktive Loot-Tabelle wurde entfernt.");
        } else {
            getPlayer().sendMessage(ChatColor.GOLD + getLootTableAlias() + " wurde als aktive Loot-Tabelle ausgewählt.");
        }
    }

    private void selectTargetAsLootTable(Player player) {
        Block targetBlock = player.getTargetBlock(TRANSPARENT_BLOCKS, LOOTTABLE_LOOKUP_DISTANCE);

        LootTable lootTable = getLootTable(targetBlock);
        if (lootTable == null) {
            player.sendMessage(ChatColor.RED + "Das Ziel ist kein Loot Objekt und kann nicht ausgewählt werden.");
            return;
        }

        setLootTable(lootTable);
    }

    private LootTable getLootTable(Block targetBlock) {
        if (targetBlock == null) {
            return null;
        }

        LootObject lootObject = getLootObjectStorage().getLootObject(targetBlock.getLocation());
        if (lootObject == null) {
            return null;
        }

        return lootObject.getLootTable();
    }

    private void createNewLootTableFromChest(PlayerInteractEvent event) {

        LootHost lootHost = getLootHostManager().getLootHost(event.getClickedBlock());
        if (lootHost == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "Der angeklickte Block ist kein gültiger Loot-Host!");
            return;
        }

        ItemStack[] contents = lootHost.getContents(event.getClickedBlock());
        Conversations.readLines(event.getPlayer(), answers -> {
                    try {
                        if (answers.length < 1) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Bitte beantworte die Fragen um eine neue Loot-Tabelle zur erstellen.");
                            return;
                        }
                        String name = answers[0];
                        if (lootTableManager.getTable(name) != null) {
                            event.getPlayer().sendMessage(ChatColor.RED + "Es gibt bereits eine Loot-Tabelle mit dem Namen '" + name + "'.");
                            return;
                        }
                        int minLoot = contents.length;
                        int maxLoot = minLoot;
                        if (answers.length < 2) {
                            event.getPlayer().sendMessage(ChatColor.GOLD + "Keine mindest Drops ausgewählt. Es droppen alle Items auf einmal.");
                        }
                        if (answers.length > 1) {
                            minLoot = Integer.parseInt(answers[1]);
                            maxLoot = minLoot;
                            if (answers.length < 3) {
                                event.getPlayer().sendMessage(ChatColor.GOLD + "Keine max Drops ausgewählt. Es droppen maximal " + maxLoot + " Items.");
                            }
                        }
                        if (answers.length > 2) {
                            maxLoot = Integer.parseInt(answers[2]);
                        }
                        setLootTable(getLootFactory().createLootTable(name, contents, minLoot, maxLoot));
                        event.getPlayer().sendMessage(ChatColor.GREEN + "Es wurde erfolgreich eine neue Loot-Tabelle erstellt: " + getLootTableAlias());
                        setLootTableActive(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
                    }
                }, "Wie soll die Loot-Tabelle heissen? (z.B.: mobs-default-drops)",
                "Wie viele Items sollen mindestens droppen?",
                "Wie viele Items sollen maximal droppen?");
    }

    private void openEditLootTableMenu(Player player) {

    }

    private void openCreateNewLootTableMenu(Player player) {

    }

    private void openChooseLootTableMenu(Player player) {

        Gui.open(player, new LootTableListUi(this));
    }

    private void convertChestToLootChest(Player player) {

        getTargetBlock(player).ifPresent(block -> createLootChest(player, block));
    }

    private void destroyLootChest(PlayerInteractEvent event) {

        LootObject lootObject = lootObjectStorage.getLootObject(event.getClickedBlock().getLocation());
        if (lootObject == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "Der angeklickte Block ist kein Loot Objekt.");
            return;
        }

        Conversations.askYesNo(event.getPlayer(), result -> lootFactory.deleteLootObject(lootObject, result),
                "Möchtest du die dazugehörige Loot-Tabelle ebenfalls löschen?",
                ChatColor.RED + "Alle anderen Loot-Kisten mit dieser Loot-Tabelle werden ebenfalls gelöscht.");
    }

    private void placeLootChest(BlockPlaceEvent event) {

        if (createLootChest(event.getPlayer(), event.getBlockPlaced())) {
            event.setCancelled(false);
        }
    }

    private boolean createLootChest(Player player, Block block) {
        if (!isLootTableActive()) {
            player.sendMessage(ChatColor.RED + "Es ist keine Loot-Tabelle aktiv.");
            return false;
        }

        LootHost lootHost = getLootHostManager().getLootHost(block);

        if (lootHost == null) {
            player.sendMessage(ChatColor.RED + "Der Block ist keine gültiger Loot-Host.");
            return false;
        }

        LootObject lootObject = getLootFactory().createLootObject(block, getLootTable());
        player.sendMessage(ChatColor.GREEN + "Loot Objekt wurde erfolgreich erstellt: " + ChatColor.GOLD + lootObject.toString());
        return true;
    }

    private Optional<Block> getTargetBlock(Player player) {
        return Optional.ofNullable(player.getTargetBlock(TRANSPARENT_BLOCKS, LOOTTABLE_LOOKUP_DISTANCE));
    }
}
