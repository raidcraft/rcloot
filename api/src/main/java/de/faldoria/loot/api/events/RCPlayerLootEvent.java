package de.faldoria.loot.api.events;

import de.faldoria.loot.api.LootObject;
import de.raidcraft.api.random.RDSObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class RCPlayerLootEvent extends PlayerEvent implements Cancellable {

    private final LootObject lootObject;
    private final RDSObject[] result;
    private boolean cancelled;

    public RCPlayerLootEvent(Player who, LootObject lootObject, RDSObject[] result) {
        super(who);
        this.lootObject = lootObject;
        this.result = result;
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
