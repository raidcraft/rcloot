package de.raidcraft.loot.util;

import de.faldoria.loot.api.LootObject;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
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

        LootChat.success(player, "Es wurde erfolgreich ein Loot-Objekt erstellt! ");
        LootChat.info(player, RaidCraft.getComponent(LootPlugin.class).getLootFactory().getObjectInfo(lootObject));
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
