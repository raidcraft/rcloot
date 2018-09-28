package de.raidcraft.loot;

import com.google.common.base.Strings;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.loot.api.object.DatabaseLootObject;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.object.LootObjectStorage;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.loot.api.table.LootTableEntry;
import de.raidcraft.loot.loottables.DatabaseLootTable;
import de.raidcraft.loot.loottables.DatabaseLootTableEntry;
import de.raidcraft.loot.tables.TLootTable;
import de.raidcraft.loot.tables.TLootTableAlias;
import de.raidcraft.loot.tables.TLootTableEntry;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 18.10.12 - 20:57
 * Description:
 */
public class LootFactory implements Component {

    public final static UUID ANY = UUID.fromString("000000f5-2100-41cc-a05d-3ed7da445841");
    public final static UUID AutomaticPlacerSurface = UUID.fromString("000000ef-b1a0-4173-9775-e5c1352a0cf9");
    public final static UUID AutomaticPlacerCave = UUID.fromString("0000008f-00ae-4368-bb33-b6c965a1f3a3");

    private LootObjectStorage lootObjectStorage;
    private LootPlugin plugin;

    public LootFactory(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootFactory.class, this
        );
        lootObjectStorage = plugin.getLootObjectStorage();
    }

    public void deleteLootObject(LootObject lootObject, boolean andTable) {

        lootObject.delete();

        if (andTable) {
            lootObject.getLootTable().delete();
        }

        lootObjectStorage.unregisterLootObject(lootObject);
    }

    public LootTable createLootTable(String alias, ItemStack[] items, int minLoot, int maxLoot) {

        List<LootTableEntry> tableEntries = new ArrayList<>();
        // at first, count total items
        int itemCount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                itemCount++;
            }
        }

        if (minLoot < 0) {
            minLoot = itemCount;
        }
        if (maxLoot < minLoot) maxLoot = minLoot;

        TLootTable tLootTable = new TLootTable();
        plugin.getRcDatabase().save(tLootTable);
        LootTable lootTable = new DatabaseLootTable(tLootTable.getId());
        lootTable.setEntries(tableEntries);
        lootTable.setMinMaxLootItems(minLoot, maxLoot);
        lootTable.save();
        // then create and add loot entries
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            TLootTableEntry tableEntry = new TLootTableEntry();
            tableEntry.setItem(RaidCraft.getItemIdString(item));
            tableEntry.setChance((int) ((1. / (double) itemCount) * 100.));
            tableEntry.setLootTable(tLootTable);
            plugin.getRcDatabase().save(tableEntry);
            DatabaseLootTableEntry entry = new DatabaseLootTableEntry(tableEntry);
            tableEntries.add(entry);
        }
        if (!Strings.isNullOrEmpty(alias)) {
            TLootTableAlias lootAlias = new TLootTableAlias();
            lootAlias.setTableAlias(alias);
            lootAlias.setLootTable(tLootTable);
            plugin.getRcDatabase().save(lootAlias);
        }
        lootTable.setEntries(tableEntries);
        lootTable.save();
        // add the loot table to the cache
        plugin.getLootTableManager().addTable(lootTable);
        return lootTable;
    }

    public LootTable createLootTable(ItemStack[] items, int minLoot, int maxLoot) {

        return createLootTable(null, items, minLoot, maxLoot);
    }

    public LootObject createLootObject(Block block, LootTable table) {
        Objects.requireNonNull(block);
        Objects.requireNonNull(table);

        DatabaseLootObject object = new DatabaseLootObject(block, table);
        object.setHostLocation(block.getLocation());

        object.save();

        // register loot object in cache
        lootObjectStorage.registerLootObject(object);

        return object;
    }

    public String getObjectInfo(LootObject lootObject) {

        return lootObject.toString();
    }
}
