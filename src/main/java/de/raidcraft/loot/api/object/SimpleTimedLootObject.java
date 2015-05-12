package de.raidcraft.loot.api.object;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.loot.LootFactory;
import de.raidcraft.loot.database.tables.LootPlayersTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 17.10.12 - 21:05
 * Description:
 */
public class SimpleTimedLootObject extends SimpleLootObject implements TimedLootObject {

    private int cooldown = 0;

    @Override
    public void setCooldown(int cooldown) {

        this.cooldown = cooldown;
    }

    @Override
    public int getCooldown() {

        return cooldown;
    }

    @Override
    public Collection<RDSObject> loot(UUID player) {

        if ((RaidCraft.getTable(LootPlayersTable.class).getLastLooted(player, getId()) * 1000 + cooldown * 1000) < System.currentTimeMillis()) {
            // remember loot if not infinite
            if (cooldown != 0 || player.equals(LootFactory.ANY)) {
                RaidCraft.getTable(LootPlayersTable.class).addEntry(player, getId(), System.currentTimeMillis() / 1000);
            }
            return getLootTable().getResult();
        }
        return new ArrayList<>();
    }
}
