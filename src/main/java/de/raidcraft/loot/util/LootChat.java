package de.raidcraft.loot.util;

import de.raidcraft.loot.object.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 18.10.12 - 21:14
 * Description:
 */
public class LootChat {

    private final static String CHAT_TAG = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Loot" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;

    public static void successfullyCreatedLootObject(Player player, LootObject lootObject) {
        String objectInfo = "";
        if (lootObject instanceof TimedLootObject) {
            objectInfo += "Timed, Cooldown: "
                    + ((SimpleTimedLootObject) lootObject).getCooldown()
                    + "s";
        } else if (lootObject instanceof TreasureLootObject) {
            objectInfo += "Schatztruhe, Stufe: " + ((SimpleTreasureLootObject) lootObject).getRewardLevel();
        }
        else if (lootObject instanceof SimpleLootObject) {
            objectInfo += "Default";
        }

        LootChat.success(player, "Es wurde erfolgreich ein Loot-Objekt erstellt! " + ChatColor.YELLOW + objectInfo);
    }

    public static void occupiedByOtherChest(Player player) {
        LootChat.warn(player, "Hier steht bereits eine Truhe im Weg!");
    }

    public static void alreadyLootObject(Player player) {
        LootChat.warn(player, "Das hier ist bereits ein Loot-Objekt!");
    }

    public static void failureDuringCreation(Player player) {
        LootChat.warn(player, "Beim erstellen ist ein Fehler aufgetreten!");
    }

    public static void success(Player player, String msg) {

        player.sendMessage(CHAT_TAG + ChatColor.GREEN + msg
        );
    }

    public static void info(Player player, String msg) {

        player.sendMessage(CHAT_TAG + ChatColor.YELLOW + msg
        );
    }

    public static void warn(Player player, String msg) {

        player.sendMessage(CHAT_TAG + ChatColor.RED + msg
        );
    }
}
