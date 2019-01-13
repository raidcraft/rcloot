package de.faldoria.loot.api.events;

import de.faldoria.loot.api.LootObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The event is called AFTER the given {@link LootObject} respawned.
 * Use the {@link RCLootObjectSpawnEvent} to intercept and cancel the spawn process.
 *
 * @see RCLootObjectSpawnEvent
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RCLootObjectSpawnedEvent extends Event {

    private final LootObject lootObject;

    public RCLootObjectSpawnedEvent(LootObject lootObject) {
        this.lootObject = lootObject;
    }

    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
