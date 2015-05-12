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
public class SimplePublicLootObject extends SimpleLootObject implements TimedLootObject, PublicLootObject {

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

        // no cooldown -> fill chest
        if ((RaidCraft.getTable(LootPlayersTable.class).getLastLooted(LootFactory.ANY, getId()) * 1000 + cooldown * 1000) < System.currentTimeMillis()) {

            // remember loot if not infinite
            if (cooldown != 0) {
                RaidCraft.getTable(LootPlayersTable.class).addEntry(LootFactory.ANY, getId(), System.currentTimeMillis() / 1000);
            }
            return getLootTable().getResult();
        }
        return new ArrayList<>();
    }
}
