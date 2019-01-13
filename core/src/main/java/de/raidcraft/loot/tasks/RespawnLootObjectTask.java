package de.raidcraft.loot.tasks;

import de.raidcraft.loot.LootObjectManager;
import lombok.Data;

import java.util.logging.Logger;

@Data
public class RespawnLootObjectTask implements Runnable {

    private final LootObjectManager lootObjectManager;
    private final Logger logger;

    public RespawnLootObjectTask(LootObjectManager lootObjectManager) {
        this.lootObjectManager = lootObjectManager;
        this.logger = lootObjectManager.getPlugin().getLogger();
    }

    @Override
    public void run() {
        long count = getLootObjectManager().respawnDestroyedLootObjects(false);
        if (count > 0) {
            getLogger().info("Respawned " + count + " Loot-Objects.");
        }
    }
}
