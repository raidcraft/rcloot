package de.faldoria.loot.api.events;

import de.faldoria.loot.api.LootObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

@Data
@EqualsAndHashCode(callSuper = true)
public class RCPlayerLootedEvent extends PlayerEvent {

    private final LootObject lootObject;
    private final ItemStack[] result;

    public RCPlayerLootedEvent(Player who, LootObject lootObject, ItemStack[] result) {
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
