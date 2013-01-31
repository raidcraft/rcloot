package de.raidcraft.loot.util;

import de.raidcraft.loot.exceptions.NoLinkedRewardTableException;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 18.11.12 - 14:48
 * Description:
 */
public class TreasureRewardLevel {

    private static Map<Integer, Integer> linkedTables = new HashMap<>();

    public static void addRewardLevel(int rewardLevel, int tableId) {

        if (tableId > 0) {
            linkedTables.put(rewardLevel, tableId);
        }
    }

    public static int getLinkedTable(int rewardLevel) throws NoLinkedRewardTableException {

        if (linkedTables.containsKey(rewardLevel)) {
            return linkedTables.get(rewardLevel);
        }
        throw new NoLinkedRewardTableException("Die angegebene Belohnungsstufe ist nicht definiert!");
    }
}
