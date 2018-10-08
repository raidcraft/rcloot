package de.raidcraft.loot.hotbar;

import com.google.common.collect.Sets;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.combatbar.api.HotbarName;
import de.raidcraft.combatbar.slots.ActionHotbarSlot;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootObjectManager;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.LootTableManager;
import de.raidcraft.loot.lootobjects.LootObject;
import de.raidcraft.loot.util.LootChat;
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

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Data
@HotbarName("loot-admin")
@EqualsAndHashCode(callSuper = true)
public class LootAdminToolbar extends Hotbar {

    private static final int LOOTTABLE_LOOKUP_DISTANCE = 10;
    private static final Set<Material> TRANSPARENT_BLOCKS = Sets.newHashSet(Material.AIR, Material.WATER, Material.LAVA);

    private final LootTableManager lootTableManager = RaidCraft.getComponent(LootTableManager.class);
    private final LootObjectManager lootObjectManager = RaidCraft.getComponent(LootObjectManager.class);
    private final LootFactory lootFactory = RaidCraft.getComponent(LootFactory.class);
    private RDSTable lootTable = null;
    private LootObject lastLootObject = null;
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
                                ChatColor.GOLD + "Linksklick: " + ChatColor.GRAY + "Loot-Tabelle des Ziels auswählen",
                                ChatColor.GOLD + "Rechtsklick: " + ChatColor.GRAY + "Information über die Loot-Tabelle des Ziels anzeigen.",
                                ChatColor.GOLD + "Inventar Links Klick: " + ChatColor.GRAY + "Andere Loot-Tabelle auswählen")
                        .item())
                .setOnSelect(this::selectTargetAsLootTable)
                .setOnInventoryLeftClick(this::openChooseLootTableMenu)
                .setOnRightClickInteract(this::showInformation)
                .setOnLeftClickInteract(this::selectTargetAsLootTable)
        );

        addHotbarSlot(new ActionHotbarSlot(
                new ItemStackBuilder(Material.CHEST)
                        .title(ChatColor.BLUE + "Loot-Tabelle auf Kiste anwenden")
                        .lore(ChatColor.GOLD + "Auswählen: " + ChatColor.GRAY + "Block in Loot-Objekt umwandeln.",
                                ChatColor.GOLD + "Linksklick: " + ChatColor.GRAY + "Block in Loot-Objekt umwandeln.",
                                ChatColor.GOLD + "Rechtsklick: " + ChatColor.GRAY + "Block in Loot-Objekt mit den letzten Einstellungen umwandeln.")
                        .item())
                .setOnSelect(this::convertToLootObject)
                .setOnLeftClickInteract(this::convertToLootObject)
                .setOnRightClickInteract(this::convertToLootObjectWithLastSettings)
        );

        addHotbarSlot(new ActionHotbarSlot(
                new ItemStackBuilder(Material.BARRIER)
                        .title(ChatColor.RED + "Loot-Objekte löschen")
                        .lore(ChatColor.GOLD + "Rechts-/Linksklick: " + ChatColor.GRAY + "Löscht das angeklickte Loot-Objekt.",
                                ChatColor.GOLD + "Auswählen: " + ChatColor.GRAY + "Löscht alle Loot-Objekte im Umkreis von X Blöcken.")
                        .item())
                .setOnSelect(this::deleteNearbyLootObjects)
                .setOnInteract(this::destroyLootChest)
                .setCancelBlockPlacement(true)
        );
    }

    private String getLootTableAlias() {
        return ChatColor.AQUA + getLootTable().getId().orElse("N/A") + " "
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

    public void setLootTable(RDSTable lootTable) {
        this.lootTable = lootTable;
        if (this.lootTable == null) {
            getPlayer().sendMessage(ChatColor.GOLD + "Aktive Loot-Tabelle wurde entfernt.");
        } else {
            getPlayer().sendMessage(ChatColor.GOLD + getLootTableAlias() + " wurde als aktive Loot-Tabelle ausgewählt.");
        }
    }

    private void selectTargetAsLootTable(Player player) {
        Block targetBlock = player.getTargetBlock(TRANSPARENT_BLOCKS, LOOTTABLE_LOOKUP_DISTANCE);

        RDSTable lootTable = getLootTable(targetBlock);
        if (lootTable == null) {
            player.sendMessage(ChatColor.RED + "Das Ziel ist kein Loot Objekt und kann nicht ausgewählt werden.");
            return;
        }

        setLootTable(lootTable);
    }

    private void showInformation(PlayerInteractEvent event) {

        Optional<LootObject> lootObject = getLootObjectManager().getLootObject(event.getClickedBlock());
        if (!lootObject.isPresent()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Der Block ist kein Loot-Objekt.");
            return;
        }

        lootObject.ifPresent(object -> event.getPlayer().sendMessage(ChatColor.GRAY + object.toString()));
    }

    private void selectTargetAsLootTable(PlayerInteractEvent event) {
        RDSTable lootTable = getLootTable(event.getClickedBlock());
        if (lootTable == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "Das Ziel ist kein Loot Objekt und kann nicht ausgewählt werden.");
            return;
        }

        setLootTable(lootTable);
    }

    private RDSTable getLootTable(Block targetBlock) {

        return this.getLootObjectManager()
                .getLootObject(targetBlock)
                .map(LootObject::getLootTable)
                .orElse(null);
    }

    private void openChooseLootTableMenu(Player player) {

        Gui.open(player, new LootTableListUi(this));
    }

    private void convertToLootObject(PlayerInteractEvent event) {
        event.setCancelled(true);
        createLootChest(event.getPlayer(), event.getClickedBlock());
    }

    private void convertToLootObject(Player player) {

        getTargetBlock(player).ifPresent(block -> createLootChest(player, block));
    }

    private void convertToLootObjectWithLastSettings(PlayerInteractEvent event) {

        if (getLastLootObject() == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "Kein letztes Loot-Objekt gefunden. Bitte konfiguriere erst ein Loot-Objekt mit dem Wizard.");
            return;
        }

        LootObject lootObject = getLootFactory().createLootObject(event.getClickedBlock(), getLootTable());
        lootObject.setEnabled(lastLootObject.isEnabled());
        lootObject.setPublicLootObject(lastLootObject.isPublicLootObject());
        lootObject.setDestroyable(lastLootObject.isDestroyable());
        lootObject.setInfinite(lastLootObject.isInfinite());
        lootObject.setCooldown(lastLootObject.getCooldown());
        lootObject.save();

        event.getPlayer().sendMessage(ChatColor.GREEN + "Loot-Objekt wurde erfolgreich erstellt: " + lootObject.toString());
    }

    private void deleteNearbyLootObjects(Player player) {

        Conversations.readLine(player, "In welchem Radius möchtest du Loot-Objekte löschen?", input -> {
            try {
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("abort")) {
                    player.sendMessage(ChatColor.RED + "Es wurden keine Loot-Objekte gelöscht.");
                    return;
                }
                int radius = Integer.parseInt(input);
                if (radius < 1) {
                    player.sendMessage(ChatColor.RED + "Radius muss größer als null sein.. Es wurden keine Loot-Objekte gelöscht.");
                    return;
                }
                Collection<LootObject> lootObjects = getLootObjectManager().getNearbyLootObjects(player.getLocation(), radius);
                Conversations.askYesNo(player, "Ja lösche " + lootObjects.size() + " im Umkreis von " + radius + " Blöcken.", "Abbrechen.", result -> {
                    if (result) {
                        lootObjects.forEach(object -> getLootObjectManager().deleteLootObject(object));
                        player.sendMessage(ChatColor.GREEN + "Es wurden " + lootObjects.size() + " Loot-Objekte im Umkreis von " + radius + " Blöcken erfolgreich gelöscht.");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Vorgang abgebrochen.");
                    }
                });
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + input + " ist keine Zahl. Es wurden keine Loot-Objekte gelöscht.");
            }
        });
    }

    private void destroyLootChest(PlayerInteractEvent event) {

        Optional<LootObject> lootObject = lootObjectManager.getLootObject(event.getClickedBlock());

        if (!lootObject.isPresent()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Der angeklickte Block ist kein Loot Objekt.");
            return;
        }

        lootObject.ifPresent(object -> {
            event.setCancelled(true);
            lootObjectManager.deleteLootObject(object);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Das Loot-Objekt wurde erfolgreich entfernt.");
        });
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

        if (this.getLootObjectManager().isLootObject(block.getLocation())) {
            player.sendMessage(ChatColor.RED + "Der Block ist bereits ein Loot-Objekt. Bitte lösche es erst um ein neues zu erstellen.");
            return false;
        }

        LootObject lootObject = getLootFactory().createLootObject(block, getLootTable());
        player.sendMessage(ChatColor.GREEN + "Loot Objekt wurde erfolgreich erstellt. " + ChatColor.GOLD);

        Conversations.startConversation(player, RaidCraft.getComponent(LootPlugin.class).config.createLootObjectConversation, CreateLootObjectConversation.class)
                .ifPresent(conversation -> {
                    conversation.setLootObject(lootObject);
                    conversation.setLootTable(lootTable);
                    conversation.setLastConfig(lastLootObject);
                    conversation.setOnEnd(object -> this.lastLootObject = object);
                });

        return true;
    }

    private Optional<Block> getTargetBlock(Player player) {
        return Optional.ofNullable(player.getTargetBlock(TRANSPARENT_BLOCKS, LOOTTABLE_LOOKUP_DISTANCE));
    }
}
