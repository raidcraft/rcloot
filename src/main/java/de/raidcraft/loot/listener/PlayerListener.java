package de.raidcraft.loot.listener;

import com.silthus.raidcraft.util.component.DateUtil;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.object.*;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.util.ChestDispenserUtil;
import de.raidcraft.loot.util.LootChat;
import de.raidcraft.loot.util.editormode.EditorModeFactory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
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
        if(editorMode.contains(event.getPlayer().getName())
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

            LootObject existingLootObject = LootFactory.inst.getLootObject(event.getClickedBlock());

            if (existingLootObject != null && event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("loot.info")) {
                printObjectInfo(event.getPlayer(), existingLootObject);
            }

            // no storage found
            if (createMode.containsKey(event.getPlayer().getName())) {
                SettingStorage settingStorage = createMode.get(event.getPlayer().getName());
                // clicked object is already loot object

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.REMOVE) {
                    if(existingLootObject == null) {
                        LootChat.warn(event.getPlayer(), "Der angeklickte Block ist kein Loot-Objekt!");
                    }
                    else {
                        LootFactory.inst.deleteLootObject(existingLootObject, true);
                        LootChat.success(event.getPlayer(), "Das Loot Objekt wurde erfolgreich gelöscht!");
                    }
                    createMode.remove(event.getPlayer().getName());   // remove create action from cache
                    return;
                }

                if (existingLootObject != null) {
                    // warn player and request deletion via command -> exit
                    LootChat.warn(event.getPlayer(), "Dies ist bereits ein Loot-Objekt und muss erst per Befehl gelöscht werden!");
                    event.setCancelled(true);
                    return;
                }

                // get items in object
                ItemStack[] items = ChestDispenserUtil.getItems(event.getClickedBlock());

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.TIMED) {
                    // create timed loot object
                    LootFactory.inst.createTimedLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , items
                            , settingStorage.getCooldown()
                            , settingStorage.getDrops());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.DEFAULT) {
                    // create default loot object
                    LootFactory.inst.createDefaultLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , items
                            , settingStorage.getDrops());
                }

                if (settingStorage.getType() == SettingStorage.SETTING_TYPE.TREASURE) {
                    // create treasure loot object
                    LootFactory.inst.createTreasureLootObject(event.getPlayer().getName(), event.getClickedBlock()
                            , settingStorage.getRewardLevel());
                }

                LootChat.successfullyCreatedLootObject(event.getPlayer(), LootFactory.inst.getLootObject(event.getClickedBlock()));

                createMode.remove(event.getPlayer().getName());   // remove create action from cache
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {

        if (LootFactory.inst.getLootObject(event.getBlock()) == null) {
            return;
        }

        LootObject lootObject = LootFactory.inst.getLootObject(event.getBlock());
        List<ItemStack> loot = lootObject.loot(LootFactory.ANY);

        if (loot.size() > 0) {
            // insert the item twice to prevent an empty dispenser
            ((Dispenser) event.getBlock().getState()).getInventory().setContents(new ItemStack[]{loot.get(0).clone(), loot.get(0).clone()});
        } else {
            event.setCancelled(true);
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

        if(!(event.getInventory().getHolder() instanceof BlockState)
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
            if(block.getType() == Material.CHEST) {
                singleChest = true;
            }
        }

        if (LootFactory.inst.getLootObject(block) == null) {
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
        LootObject lootObject = LootFactory.inst.getLootObject(block);

        // check if locked
        if (inventoryLocks.containsValue(lootObject)) {
            LootChat.warn((Player) entity, "Du musst kurz warten bis ein anderer Spieler fertig gelootet hat!");
            event.setCancelled(true);
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

        // halve the loot if single chest (smaller chance for single chests)
        if(singleChest && loot.size() > 1 && !admin) {
            loot = loot.subList(0, loot.size()/2);
        }

        // cut loot if too many items
        if(loot.size() > event.getInventory().getSize()) {
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
            if(adminMode.contains(event.getPlayer().getName())) {
                return;
            }

            if((lootObject instanceof TimedLootObject) && (((TimedLootObject)lootObject).getCooldown() == 0)) {
                return;
            }
            for(ItemStack itemStack : event.getInventory().getContents()) {
                if(itemStack != null && itemStack.getType() != Material.AIR) {
                    event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), itemStack);
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
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    private void printObjectInfo(Player player, LootObject lootObject) {
        String info = "Typ: ";
        if (lootObject instanceof TimedLootObject) {
            info += "Timed-Loot-Objekt, Cooldown: "
                    + ((SimpleTimedLootObject) lootObject).getCooldown()
                    + "s";
        } else if (lootObject instanceof TreasureLootObject) {
            info += "Schatztruhe, Stufe: " + ((SimpleTreasureLootObject) lootObject).getRewardLevel();
        }
        else if (lootObject instanceof SimpleLootObject) {
            info += "Default-Loot-Objekt";
        }

        info += ", Drops: " + lootObject.getLootTable().getMinLootItems() + "-" + lootObject.getLootTable().getMaxLootItems() + ", Ersteller: " + lootObject.getCreator()
                + ", Erstelldatum: " + DateUtil.getDateString(lootObject.getCreated() * 1000);
        LootChat.info(player, info);
    }
}
