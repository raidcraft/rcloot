package de.raidcraft.loot.object;

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
