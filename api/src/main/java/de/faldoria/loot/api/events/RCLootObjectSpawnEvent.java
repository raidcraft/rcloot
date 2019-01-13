package de.faldoria.loot.api.events;

import de.faldoria.loot.api.LootObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
@EqualsAndHashCode(callSuper = true)
public class RCLootObjectSpawnEvent extends Event implements Cancellable {

    private final LootObject lootObject;
    private boolean force;
    private boolean cancelled;

    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
