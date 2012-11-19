package de.raidcraft.loot.object;

import com.silthus.raidcraft.util.component.database.ComponentDatabase;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.database.tables.LootPlayersTable;
import de.raidcraft.loot.table.LootTableEntry;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 17.10.12 - 21:05
 * Description:
 */
public class SimpleTimedLootObject extends SimpleLootObject implements TimedLootObject {

    int cooldown = 0;

    @Override
    public void setCooldown(int cooldown) {

        this.cooldown = cooldown;
    }

    @Override
    public int getCooldown() {

        return cooldown;
    }

    @Override
    public List<ItemStack> loot(String player) {

        List<ItemStack> loot = new ArrayList<>();

        if ((ComponentDatabase.INSTANCE.getTable(LootPlayersTable.class).getLastLooted(player, getId()) * 1000 + cooldown * 1000) < System.currentTimeMillis()) {
            for (LootTableEntry entry : getLootTable().loot()) {
                loot.add(entry.getItem());
            }

            // remember loot if not infinite dispenser
            if (cooldown != 0 || player != LootFactory.ANY) {
                ComponentDatabase.INSTANCE.getTable(LootPlayersTable.class).addEntry(player, getId(), System.currentTimeMillis() / 1000);
            }
        }
        return loot;
    }
}
