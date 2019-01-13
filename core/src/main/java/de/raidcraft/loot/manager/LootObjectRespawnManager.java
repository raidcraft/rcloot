package de.raidcraft.loot.manager;

import de.raidcraft.api.Component;
import de.raidcraft.loot.LootObjectManager;
import de.raidcraft.loot.tasks.RespawnLootObjectTask;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

@Data
public class LootObjectRespawnManager implements Component {

    private final LootObjectManager lootObjectManager;
    private final BukkitTask respawnTask;

    public LootObjectRespawnManager(LootObjectManager lootObjectManager) {
        this.lootObjectManager = lootObjectManager;
        respawnTask = Bukkit.getScheduler().runTaskTimer(lootObjectManager.getPlugin(),
                new RespawnLootObjectTask(lootObjectManager),
                lootObjectManager.getPlugin().config.respawnIntervalInTicks,
                lootObjectManager.getPlugin().config.respawnIntervalInTicks
        );
    }

    public void enable() {

    }

    public void reload() {

    }
}
