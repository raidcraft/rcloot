package de.raidcraft.loot;

/**
 * Author: Philip
 * Date: 29.10.12 - 05:20
 * Description:
 */
public class SettingStorage {

    public final static int ALL = -1;
    private SETTING_TYPE type;
    private int cooldown = 0;
    private int drops = ALL;
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

    public int getDrops() {

        return drops;
    }

    public SettingStorage setDrops(int drops) {

        this.drops = drops;
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
        DEFAULT,
        TREASURE,
        REMOVE
    }
}
