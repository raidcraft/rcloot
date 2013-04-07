package de.raidcraft.loot.listener;

import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.editor.EditorModeFactory;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.object.PublicLootObject;
import de.raidcraft.loot.object.TimedLootObject;
import de.raidcraft.loot.object.TreasureLootObject;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 18.10.12 - 06:25
 * Description:
 */
public class PlayerListener implements Listener {

    public static Map<String, SettingStorage> createMode = new HashMap<>();
    public static List<String> editorMode = new ArrayList<>();
    public static List<String> adminMode = new ArrayList<>();
    private Map<String, LootObject> inventoryLocks = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        // editor mode
        if (editorMode.contains(event.getPlayer().getName())
                && event.getItem() != null
                && event.getClickedBlock() != null
                && EditorModeFactory.INSTANCE.isEditorBlock(event.getItem())) {
            EditorModeFactory.INSTANCE.getEditorItem(event.getItem()).run(event);
            event.setCancelled(true);
            return;
        }

        // if dispenser or chest clicked
        if (event.getClickedBlock() != null
                && (event.getClickedBlock().getType() == Material.DISPENSER
                || event.getClickedBlock().getType() == Material.CHEST)) {

            LootFactory.INST.loadObjects(event.getPlayer().getLocation().getChunk());

            LootObject existingLootObject = LootFactory.INST.getLootObject(event.getClickedBlock().getLocation());

            if (existingLootObject != null && event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("loot.info")) {
                LootChat.info(event.getPlayer(), LootFactory.INST.getObjectInfo(event.getPlayer(), existingLootObject));
            }

            // no storage found
            if (createMode.containsKey(event.getPlayer().getName())) {
                SettingStorage settingStorage = createMode.get(event.getPlayer().getName());
                // clicked object is already loot object

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.REMOVE) {
                    if (existingLootObject == null) {
                        LootChat.warn(event.getPlayer(), "Der angeklickte Block ist kein Loot-Objekt!");
                    } else {
                        LootFactory.INST.deleteLootObject(existingLootObject, true);
                        LootChat.success(event.getPlayer(), "Das Loot Objekt wurde erfolgreich gelöscht!");
                    }
                    createMode.remove(event.getPlayer().getName());   // remove create action from cache
                    return;
                }

                if (existingLootObject != null) {
                    // warn player and request deletion via command -> exit
                    LootChat.warn(event.getPlayer(), "Dies ist bereits ein Loot-Objekt und muss erst per Befehl gelöscht werden!");
                    createMode.remove(event.getPlayer().getName());
                    event.setCancelled(true);
                    return;
                }

                // get items in object
                ItemStack[] items = ChestDispenserUtil.getItems(event.getClickedBlock());

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.TIMED) {
                    // create timed loot object
                    LootFactory.INST.createTimedLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , items
                            , settingStorage.getCooldown()
                            , settingStorage.getDrops());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.PUBLIC) {
                    // create public loot object
                    LootFactory.INST.createPublicLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , items
                            , settingStorage.getCooldown());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.DEFAULT) {
                    // create default loot object
                    LootFactory.INST.createDefaultLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , items
                            , settingStorage.getDrops());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.TREASURE) {
                    // create treasure loot object
                    LootFactory.INST.createTreasureLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , settingStorage.getRewardLevel());
                }

                LootChat.successfullyCreatedLootObject(event.getPlayer(), LootFactory.INST.getLootObject(event.getClickedBlock().getLocation()));

                createMode.remove(event.getPlayer().getName());   // remove create action from cache
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

        if (event.getInventory().getType() != InventoryType.DISPENSER
                && event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (!(event.getInventory().getHolder() instanceof BlockState)
                && !(event.getInventory().getHolder() instanceof DoubleChest)) {
            return;
        }

        Block block;
        boolean singleChest = false;

        if (event.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) event.getInventory().getHolder();
            block = doubleChest.getLocation().getBlock();
        } else {
            block = ((BlockState) event.getInventory().getHolder()).getBlock();
            if (block.getType() == Material.CHEST) {
                singleChest = true;
            }
        }

        if (LootFactory.INST.getLootObject(block.getLocation()) == null) {
            return;
        }

        HumanEntity entity = event.getPlayer();

        if (!(entity instanceof Player)) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.DISPENSER && !entity.hasPermission("loot.admin")) {
            LootChat.warn((Player) entity, "Du hast keine Rechte um Loot-Dispenser zu öffnen!");
            event.setCancelled(true);
            return;
        }

        List<ItemStack> loot;
        LootObject lootObject = LootFactory.INST.getLootObject(block.getLocation());

        // check if locked
        if (inventoryLocks.containsValue(lootObject)) {
            LootChat.warn((Player) entity, "Du musst kurz warten bis ein anderer Spieler fertig gelootet hat!");
            event.setCancelled(true);
            return;
        }


        // fill public loot chest if cooldown over
        if(lootObject instanceof PublicLootObject) {
            loot = lootObject.loot(entity.getName());
            if(loot.size() > 0) {
                event.getInventory().setContents(loot.toArray(new ItemStack[loot.size()]));
            }
            return;
        }

        // lock loot object
        inventoryLocks.put(entity.getName(), lootObject);
        boolean admin = false;
        if (adminMode.contains(entity.getName())) {
            admin = true;
            LootChat.info((Player) entity, "Du befindest dich im Admin-Modus!");
            // fill loot object with all table entries
            List<LootTableEntry> entries = lootObject.getLootTable().getEntries();
            loot = new ArrayList<>();
            for (LootTableEntry entry : entries) {
                loot.add(entry.getItem());
            }
        } else {
            loot = lootObject.loot(entity.getName());
        }
        // set loot
        event.getInventory().clear();

        // halve the loot if single chest (smaller chance for single treasure chests)
        if ((lootObject instanceof TreasureLootObject) && singleChest && loot.size() > 1 && !admin) {
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

        if (inventoryLocks.containsKey(event.getPlayer().getName())) {
            LootObject lootObject = inventoryLocks.get(event.getPlayer().getName());
            inventoryLocks.remove(event.getPlayer().getName());

            // drop not cleared items if loot object isn't infinite
            if (!adminMode.contains(event.getPlayer().getName())) {

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

        if (inventoryLocks.containsKey(event.getPlayer().getName())) {
            inventoryLocks.remove(event.getPlayer().getName());
        }
        editorMode.remove(event.getPlayer().getName());
        adminMode.remove(event.getPlayer().getName());
    }
}
