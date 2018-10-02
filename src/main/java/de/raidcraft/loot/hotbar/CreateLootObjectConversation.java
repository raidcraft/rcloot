package de.raidcraft.loot.hotbar;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.answer.InputAnswer;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.conversations.conversations.PlayerConversation;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.table.LootTable;
import de.raidcraft.util.ConfigUtil;
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Data
public class CreateLootObjectConversation extends PlayerConversation {

    private RDSTable lootTable;
    private LootObject lootObject;
    private LootObject lastConfig;
    private Consumer<LootObject> onEnd;

    public CreateLootObjectConversation(Player player, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {
        super(player, conversationTemplate, conversationHost);
    }

    @Override
    protected boolean onStart() {
        if (lootObject == null || lootTable == null) return false;


        InputAnswer inputAnswer = new InputAnswer("Cooldown in Sekunden (-1 für keinen Cooldown)")
                .setInputListener(input -> lootObject.setCooldown(Integer.parseInt(input)));
        inputAnswer.addAction(Action.endConversation(ConversationEndReason.ENDED));
        Stage cooldownStage = Conversations.createStage(this, "Was für einen Cooldown soll das Loot-Objekt haben?",
                inputAnswer
        );

        setCurrentStage(Conversations.createStage(this, "Welche Art von Loot-Objekt möchtest du erstellen?",
                Answer.of("Öffentlich - Alle Spieler teilen sich den Loot.", (type, config) -> lootObject.setPublicLootObject(true), Action.changeStage(cooldownStage)),
                Answer.of("Privat - Jeder Spieler kann unabhängig looten.", (type, config) -> lootObject.setPublicLootObject(false), Action.changeStage(cooldownStage)),
                Answer.of("Unendlich - Jeder kann unendlich oft daraus looten.", (type, config) -> lootObject.setInfinite(true), Action.changeStage(cooldownStage)),
                Answer.of("Standard Werte übernehmen: einmalig für jeden Spieler lootbar.", (type, config) -> lootObject.toString(), Action.endConversation(ConversationEndReason.ENDED)),
                lastConfig != null ? Answer.of("Konfiguration des letzten Loot-Objekts übernehmen. "
                        + (lastConfig.isPublicLootObject() ? ChatColor.GREEN + "Öffentlich" : ChatColor.DARK_PURPLE + "Privat")
                        + ChatColor.GOLD + " - "
                        + (lastConfig.isInfinite() ? ChatColor.AQUA + "Unendlich" : "")
                        + ChatColor.GOLD + " - "
                        + ChatColor.GRAY + "Cooldown: " + TimeUtil.getFormattedTime(lastConfig.getCooldown()), (type, config) -> {
                    lootObject.setCooldown(lastConfig.getCooldown());
                    lootObject.setInfinite(lastConfig.isInfinite());
                    lootObject.setPublicLootObject(lastConfig.isPublicLootObject());
                }, Action.endConversation(ConversationEndReason.ENDED)) : null
        ));

        return true;
    }

    @Override
    protected void onEnd(ConversationEndReason reason) {

        lootObject.save();
        sendMessage(ChatColor.GREEN + "Loot-Objekt wurde gespeichert.");
        if (onEnd != null) onEnd.accept(lootObject);
    }
}
