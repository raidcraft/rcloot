package de.raidcraft.loot.api.object;

/**
 * Author: Philip
 * Date: 17.10.12 - 21:03
 * Description:
 */
public interface TimedLootObject extends LootObject {

    void setCooldown(int cooldown);

    int getCooldown();
}
