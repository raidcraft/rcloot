package de.raidcraft.loot.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 18.10.12 - 21:14
 * Description:
 */
public class LootChat {

    private final static String CHAT_TAG = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Loot" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;

    public static void successfullyCreatedLootObject(Player player) {
        LootChat.success(player, "Es wurde erfolgreich ein Loot-Objekt erstellt!");
    }

    public static void occupiedByOtherChest(Player player) {
        LootChat.warn(player, "Hier steht bereits eine Truhe im Weg!");
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
