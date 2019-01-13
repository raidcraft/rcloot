package de.faldoria.loot.playerobjects;

import de.faldoria.loot.api.LootObject;
import de.faldoria.loot.api.events.RCLootObjectSpawnedEvent;
import de.faldoria.loot.api.events.RCPlayerLootedEvent;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.components.BukkitComponent;
import de.raidcraft.api.components.ComponentInformation;
import de.raidcraft.loot.LootObjectManager;
import de.raidcraft.util.TimeUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

@ComponentInformation(
        value = "player-objects",
        friendlyName = "Player Objects",
        desc = "Enables loot objects that are only shown to the players that can loot them.",
        authors = "Silthus"
)
public class PlayerObjectsComponent extends BukkitComponent implements Listener, Runnable {

    @Getter
    private LootObjectManager lootObjectManager;
    private BukkitTask task;

    @Override
    public void enable() {

        this.lootObjectManager = RaidCraft.getComponent(LootObjectManager.class);

        if (getLootObjectManager() != null) {
            registerEvents(this);
            long taskInterval = TimeUtil.parseTimeAsTicks(getConfiguration().getString("task-interval", "5s"));
            task = Bukkit.getScheduler().runTaskTimer(getPlugin(), this, taskInterval, taskInterval);
        } else {
            getPlugin().getLogger().warning("LootObjectManager not found! Not enabling player based loot objects.");
        }
    }

    @Override
    public void disable() {

        if (getLootObjectManager() != null) {
            unregisterEvents(this);
            task.cancel();
            task = null;
            lootObjectManager = null;
        }
    }

    @Override
    public void run() {

        Collection<LootObject> lootObjects = getLootObjectManager().getLootObjects();
        lootObjects.forEach(this::hideLootObject);
        lootObjects.forEach(this::showLootObject);
    }

    @EventHandler()
    public void onLootObjectRespawn(RCLootObjectSpawnedEvent event) {

        if (!getConfiguration().getBoolean("checks.respawn", true)) {
            return;
        }

        hideLootObject(event.getLootObject());
        showLootObject(event.getLootObject());
    }

    @EventHandler
    public void onLootObjectLooted(RCPlayerLootedEvent event) {
        if (!getConfiguration().getBoolean("checks.loot", true)) {
            return;
        }

        hideLootObject(event.getLootObject());
    }

    private void showLootObject(LootObject lootObject) {

        lootObject.getHostLocation().getWorld().getPlayers()
                .stream()
                .filter(player -> lootObject.canLoot(player.getUniqueId()))
                .forEach(player -> {
                    BlockData blockData = lootObject.getBlockData().orElse(Bukkit.createBlockData(lootObject.getMaterial()));
                    player.sendBlockChange(lootObject.getHostLocation(), blockData);
                });
    }

    private void hideLootObject(LootObject lootObject) {

        lootObject.getHostLocation().getWorld().getPlayers()
                .stream()
                .filter(player -> !lootObject.canLoot(player.getUniqueId()))
                .forEach(player -> player.sendBlockChange(lootObject.getHostLocation(), Bukkit.createBlockData(Material.AIR)));
    }
}
