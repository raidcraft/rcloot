package de.raidcraft.loot.object;

/**
 * Author: Philip
 * Date: 17.10.12 - 21:03
 * Description:
 */
public interface CooldownLootObject extends LootObject {

    public void setCooldown(int cooldown);

    public int getCooldown();
}
