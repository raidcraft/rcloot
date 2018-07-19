package de.raidcraft.loot.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.api.object.*;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.commands.LootTableCreation;
import de.raidcraft.loot.editor.EditorModeFactory;
import de.raidcraft.loot.loothost.LootHost;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:25
 * Description:
 */
public class PlayerListener implements Listener {

    public static Map<UUID, SettingStorage> createMode = new HashMap<>();
    public static Map<UUID, LootTableCreation> createLootTable = new HashMap<>();
    public static List<UUID> editorMode = new ArrayList<>();
    public static List<UUID> adminMode = new ArrayList<>();
    private Map<UUID, LootObject> inventoryLocks = new HashMap<>();
    private Map<UUID, List<LootTableEntry>> createLootTableEntries = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onLootTableCreate(PlayerInteractEvent event) {

        if (!createLootTable.containsKey(event.getPlayer().getUniqueId()) || event.getClickedBlock() == null) {
            return;
        }
        // if lootable block clicked
        LootHost lootHost = RaidCraft.getComponent(LootPlugin.class).getLootHostManager().getLootHost(event.getClickedBlock().getType());
        if (lootHost != null) {
            LootFactory lootFactory = RaidCraft.getComponent(LootPlugin.class).getLootFactory();
            LootTableCreation creation = createLootTable.remove(event.getPlayer().getUniqueId());
            LootTable table = lootFactory.createLootTable(
                    creation.getAlias(),
                    lootHost.getContents(event.getClickedBlock()),
                    creation.getMinAmount(),
                    creation.getMaxAmount());
            table.save();
            event.getPlayer().sendMessage(ChatColor.GREEN + "Loot Table was created with the id: " + table.getId());
            event.setCancelled(true);
            // lets store the loot table entry creation
            if (table.getEntries().size() > 0) {
                createLootTableEntries.put(event.getPlayer().getUniqueId(), new ArrayList<>(table.getEntries()));
                event.getPlayer().sendMessage(ChatColor.GREEN + "Bitte gebe die Chance für "
                        + ItemUtils.toString(table.getEntries().get(0).getItem()) + " an: ");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        if (createLootTableEntries.containsKey(event.getPlayer().getUniqueId())) {
            try {
                LootTableEntry entry = createLootTableEntries.get(event.getPlayer().getUniqueId()).get(0);
                double chance = Double.parseDouble(event.getMessage());
                entry.setChance(chance);
                entry.save();
                createLootTableEntries.get(event.getPlayer().getUniqueId()).remove(0);
                if (createLootTableEntries.get(event.getPlayer().getUniqueId()).size() > 0) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Bitte gebe die Chance für "
                            + ItemUtils.toString(createLootTableEntries.get(event.getPlayer().getUniqueId()).get(0).getItem()) + " an: ");
                } else {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Bitte setzte nun noch falls gewollt die Qualitäts Chancen in der Datenbank.");
                    createLootTableEntries.remove(event.getPlayer().getUniqueId());
                }
            } catch (NumberFormatException e) {
                event.getPlayer().sendMessage(ChatColor.RED + "Du musst eine Chance in Dezimal Notation angeben, z.B. 10% = 0.10");
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null) return;

        // editor mode
        if (editorMode.contains(event.getPlayer().getUniqueId())
                && event.getItem() != null
                && event.getClickedBlock() != null
                && EditorModeFactory.INSTANCE.isEditorBlock(event.getItem())) {
            EditorModeFactory.INSTANCE.getEditorItem(event.getItem()).run(event);
            event.setCancelled(true);
            return;
        }

        // if lootable block clicked
        LootHost lootHost = RaidCraft.getComponent(LootPlugin.class).getLootHostManager().getLootHost(event.getClickedBlock().getType());
        if (lootHost != null) {

            LootFactory lootFactory = RaidCraft.getComponent(LootPlugin.class).getLootFactory();
            LootObjectStorage lootObjectStorage = RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage();
            LootObject existingLootObject = lootObjectStorage.getLootObject(event.getClickedBlock().getLocation());

            if (existingLootObject != null && event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("loot.info")) {
                LootChat.info(event.getPlayer(), lootFactory.getObjectInfo(existingLootObject));
            }

            // no storage found
            if (createMode.containsKey(event.getPlayer().getUniqueId())) {
                SettingStorage settingStorage = createMode.get(event.getPlayer().getUniqueId());
                // clicked object is already loot object

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.REMOVE) {
                    if (existingLootObject == null) {
                        LootChat.warn(event.getPlayer(), "Der angeklickte Block ist kein Loot-Objekt!");
                    } else {
                        lootFactory.deleteLootObject(existingLootObject, true);
                        LootChat.success(event.getPlayer(), "Das Loot Objekt wurde erfolgreich gelöscht!");
                    }
                    createMode.remove(event.getPlayer().getUniqueId());   // remove create action from cache
                    return;
                }

                if (existingLootObject != null) {
                    // warn player and request deletion via command -> exit
                    LootChat.warn(event.getPlayer(), "Dies ist bereits ein Loot-Objekt und muss erst per Befehl gelöscht werden!");
                    createMode.remove(event.getPlayer().getUniqueId());
                    event.setCancelled(true);
                    return;
                }

                // get items in object
                ItemStack[] items = lootHost.getContents(event.getClickedBlock());

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.TIMED) {
                    // create timed loot object
                    lootFactory.createTimedLootObject(event.getPlayer().getUniqueId(), event.getClickedBlock()
                            , items
                            , settingStorage.getCooldown()
                            , settingStorage.getMinLoot(),
                            settingStorage.getMaxLoot());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.PUBLIC) {
                    // create public loot object
                    lootFactory.createPublicLootObject(event.getPlayer().getUniqueId(), event.getClickedBlock()
                            , items
                            , settingStorage.getCooldown(),
                            settingStorage.getMinLoot(),
                            settingStorage.getMaxLoot());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.DEFAULT) {
                    // create default loot object
                    lootFactory.createDefaultLootObject(event.getPlayer().getUniqueId(), event.getClickedBlock()
                            , items,
                            settingStorage.getMinLoot(),
                            settingStorage.getMaxLoot());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.TREASURE) {
                    // create treasure loot object
                    lootFactory.createTreasureLootObject(event.getPlayer().getUniqueId(), event.getClickedBlock()
                            , settingStorage.getRewardLevel());
                }

                LootChat.successfullyCreatedLootObject(event.getPlayer(), lootObjectStorage.getLootObject(event.getClickedBlock().getLocation()));

                createMode.remove(event.getPlayer().getUniqueId());   // remove create action from cache
                event.setCancelled(true);
                return;
            }
        }
    }

    /*
     * Prevent inventory open if inventory holder is loot object
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {

        LootHost lootHost = RaidCraft.getComponent(LootPlugin.class).getLootHostManager().getLootHost(event.getInventory());
        if (lootHost == null) return;

        Block block = lootHost.getBlock(event.getInventory());
        if (block == null) return;

        LootObjectStorage lootObjectStorage = RaidCraft.getComponent(LootPlugin.class).getLootObjectStorage();
        LootObject lootObject = lootObjectStorage.getLootObject(block.getLocation());
        if (lootObject == null) {
            return;
        }

        HumanEntity entity = event.getPlayer();

        if (!(entity instanceof Player)) {
            return;
        }

        if (entity.getGameMode() == GameMode.SPECTATOR || entity.getGameMode() == GameMode.CREATIVE) {
            if (!adminMode.contains(entity.getUniqueId())) {
                LootChat.warn((Player) entity, "Du kannst in diesem Gamemode keine Lootkisten öffnen.");
                event.setCancelled(true);
                return;
            }
        }

        if (!lootHost.canBeOpened() && !entity.hasPermission("loot.admin")) {
            LootChat.warn((Player) entity, "Du hast keine Rechte um Loot-Dispenser zu öffnen!");
            event.setCancelled(true);
            return;
        }

        List<ItemStack> loot;

        // check if locked
        if (inventoryLocks.containsValue(lootObject)) {
            LootChat.warn((Player) entity, "Du musst kurz warten bis ein anderer Spieler fertig gelootet hat!");
            event.setCancelled(true);
            return;
        }

        // fill public loot chest if cooldown over
        if (lootObject instanceof PublicLootObject) {
            loot = lootObject.loot(entity.getUniqueId());
            if (loot.size() > 0) {
                event.getInventory().setContents(loot.toArray(new ItemStack[loot.size()]));
            }
            return;
        }

        // lock loot object
        inventoryLocks.put(entity.getUniqueId(), lootObject);
        boolean admin = false;
        if (adminMode.contains(entity.getUniqueId())) {
            admin = true;
            LootChat.info((Player) entity, "Du befindest dich im Admin-Modus!");
            // fill loot object with all table entries
            List<LootTableEntry> entries = lootObject.getLootTable().getEntries();
            loot = new ArrayList<>();
            for (LootTableEntry entry : entries) {
                loot.add(entry.getItem());
            }
        } else {
            loot = lootObject.loot(entity.getUniqueId());
        }
        // set loot
        event.getInventory().clear();

        // halve the loot if single chest (smaller chance for single treasure chests)
        if ((lootObject instanceof TreasureLootObject) && lootHost.halfTreasureChance(event.getInventory()) && loot.size() > 1 && !admin) {
            loot = loot.subList(0, loot.size() / 2);
        }

        // cut loot if too many items
        if (loot.size() > event.getInventory().getSize()) {
            loot = loot.subList(0, event.getInventory().getSize());
        }

        event.getInventory().setContents(loot.toArray(new ItemStack[loot.size()]));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (inventoryLocks.containsKey(event.getPlayer().getUniqueId())) {
            LootObject lootObject = inventoryLocks.get(event.getPlayer().getUniqueId());
            inventoryLocks.remove(event.getPlayer().getUniqueId());

            // drop not cleared items if loot object isn't infinite
            if (!adminMode.contains(event.getPlayer().getUniqueId())) {

                if (!(lootObject instanceof TimedLootObject) || (((TimedLootObject) lootObject).getCooldown() != 0)) {
                    for (ItemStack itemStack : event.getInventory().getContents()) {
                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                            event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
                        }
                    }
                }
            }

            // fill dispenser otherwise the dispenser event won't be called
            if (event.getInventory().getType() == InventoryType.DISPENSER) {
                List<ItemStack> loot = lootObject.loot(LootFactory.ANY);
                event.getInventory().clear();
                if (loot.size() == 0) loot.add(new ItemStack(Material.STONE, 1));    // force add item if database error occurred
                for (ItemStack item : loot) {
                    // create item stack
                    ItemStack newItemStack = item.clone();
                    event.getInventory().addItem(newItemStack);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuiet(PlayerQuitEvent event) {

        if (inventoryLocks.containsKey(event.getPlayer().getUniqueId())) {
            inventoryLocks.remove(event.getPlayer().getUniqueId());
        }
        editorMode.remove(event.getPlayer().getUniqueId());
        adminMode.remove(event.getPlayer().getUniqueId());
        createMode.remove(event.getPlayer().getUniqueId());
    }
}
