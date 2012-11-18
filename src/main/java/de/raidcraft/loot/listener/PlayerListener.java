package de.raidcraft.loot.listener;

import com.silthus.raidcraft.util.component.DateUtil;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.SettingStorage;
import de.raidcraft.loot.object.*;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.util.LootChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.*;
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
    private Map<String, LootObject> inventoryLocks = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // if dispenser or chest clicked
        if (event.getClickedBlock() != null
                && (event.getClickedBlock().getType() == Material.DISPENSER
                || event.getClickedBlock().getType() == Material.CHEST)) {

            LootObject existingLootObject = LootFactory.inst.getLootObject(event.getClickedBlock());

            // no storage found
            if (!createMode.containsKey(event.getPlayer().getName())) {
                // show infos about loot object
                if (existingLootObject != null && event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().hasPermission("loot.info")) {

                    String info = "Typ: ";
                    if (existingLootObject instanceof CooldownLootObject) {
                        info += "Timed-Loot-Objekt, Cooldown: "
                                + ((SimpleTimedLootObject) existingLootObject).getCooldown()
                                + "s";
                    } else if (existingLootObject instanceof SimpleLootObject) {
                        info += "Default-Loot-Objekt";
                    }
                    else if (existingLootObject instanceof TreasureLootObject) {
                        info += "Schatztruhe, Stufe: " + ((SimpleTreasureLootObject) existingLootObject).getRewardLevel();
                    }

                    info += ", Drops: " + existingLootObject.getLootTable().getMinLootItems() + ", Ersteller: " + existingLootObject.getCreator()
                            + ", Erstelldatum: " + DateUtil.getDateString(existingLootObject.getCreated() * 1000);
                    LootChat.info(event.getPlayer(), info);
                }
            }
            // player has a setting storage
            else {
                SettingStorage settingStorage = createMode.get(event.getPlayer().getName());
                // clicked object is already loot object
                if (existingLootObject != null) {
                    if (settingStorage.getType() == SettingStorage.SETTING_TYPE.REMOVE) {
                        LootFactory.inst.deleteLootObject(existingLootObject, true);
                        LootChat.success(event.getPlayer(), "Das Loot Objekt wurde erfolgreich gelöscht!");
                        createMode.remove(event.getPlayer().getName());   // remove create action from cache
                        return;
                    }
                    // warn player and request deletion via command -> exit
                    LootChat.warn(event.getPlayer(), "Dies ist bereits ein Loot-Objekt und muss erst per Befehl gelöscht werden!");
                    return;
                }

                // get items in object
                ItemStack[] items = new ItemStack[]{null};
                if (event.getClickedBlock().getState() instanceof Chest) {
                    items = ((Chest) event.getClickedBlock().getState()).getInventory().getContents();
                    ((Chest) event.getClickedBlock().getState()).getInventory().clear();
                }
                if (event.getClickedBlock().getState() instanceof Dispenser) {
                    items = ((Dispenser) event.getClickedBlock().getState()).getInventory().getContents();
                }


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
                            , settingStorage.getDrops()
                            , settingStorage.getRewardLevel());
                }

                LootChat.success(event.getPlayer(), "Es wurde erfolgreich ein Loot-Objekt erstellt!");

                createMode.remove(event.getPlayer().getName());   // remove create action from cache
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && LootFactory.inst.getLootObject(event.getClickedBlock()) != null) {

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

        Block block;

        if (event.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) event.getInventory().getHolder();
            block = doubleChest.getLocation().getBlock();
        } else {
            block = ((BlockState) event.getInventory().getHolder()).getBlock();
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

        if (entity.hasPermission("loot.admin")) {
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
        event.getInventory().setContents(loot.toArray(new ItemStack[loot.size()]));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (inventoryLocks.containsKey(event.getPlayer().getName())) {
            inventoryLocks.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuiet(PlayerQuitEvent event) {

        if (inventoryLocks.containsKey(event.getPlayer().getName())) {
            inventoryLocks.remove(event.getPlayer().getName());
        }
    }
}
