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
public class SimpleTreasureLootObject extends SimpleLootObject implements TreasureLootObject {

    private int rewardLevel = 0;

    @Override
    public void setRewardLevel(int rewardLevel) {

        this.rewardLevel = rewardLevel;
    }

    @Override
    public int getRewardLevel() {

        return rewardLevel;
    }
}
