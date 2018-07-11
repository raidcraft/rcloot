package de.raidcraft.loot;

import lombok.Data;

/**
 * Author: Philip
 * Date: 29.10.12 - 05:20
 * Description:
 */
@Data
public class SettingStorage {

    public final static int ALL = -1;
    private SETTING_TYPE type;
    private int cooldown = 0;
    private int minLoot = ALL;
    private int maxLoot = minLoot;
    private int rewardLevel = 0;

    public SettingStorage(SETTING_TYPE type) {

        this.type = type;
    }

    public int getCooldown() {

        return cooldown;
    }

    public SettingStorage setCooldown(int cooldown) {

        this.cooldown = cooldown;
        return this;
    }

    public int getMinLoot() {

        return minLoot;
    }

    public SettingStorage setMinLoot(int minLoot) {

        this.minLoot = minLoot;
        return this;
    }

    public SettingStorage setRewardLevel(int level) {

        this.rewardLevel = level;
        return this;
    }

    public int getRewardLevel() {

        return rewardLevel;
    }

    public SETTING_TYPE getType() {

        return type;
    }

    public enum SETTING_TYPE {
        TIMED,
        PUBLIC,
        DEFAULT,
        TREASURE,
        REMOVE
    }
}
