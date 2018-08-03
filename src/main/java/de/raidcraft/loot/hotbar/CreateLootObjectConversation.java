package de.raidcraft.loot.hotbar;

import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.conversations.Conversations;
import de.raidcraft.api.conversations.answer.Answer;
import de.raidcraft.api.conversations.answer.InputAnswer;
import de.raidcraft.api.conversations.conversation.ConversationEndReason;
import de.raidcraft.api.conversations.conversation.ConversationTemplate;
import de.raidcraft.api.conversations.host.ConversationHost;
import de.raidcraft.api.conversations.stage.Stage;
import de.raidcraft.conversations.conversations.PlayerConversation;
import de.raidcraft.loot.api.object.LootObject;
import de.raidcraft.loot.api.table.LootTable;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class CreateLootObjectConversation extends PlayerConversation {

    private LootTable lootTable;
    private LootObject lootObject;

    public CreateLootObjectConversation(Player player, ConversationTemplate conversationTemplate, ConversationHost conversationHost) {
        super(player, conversationTemplate, conversationHost);
    }

    @Override
    protected boolean onStart() {
        if (lootObject == null || lootTable == null) return false;


        Stage cooldownStage = Conversations.createStage(this, "Was für einen Cooldown soll das Loot-Objekt haben?",
                new InputAnswer("Cooldown in Sekunden (-1 für keinen Cooldown)")
                        .setInputListener(input -> lootObject.setCooldown(Integer.parseInt(input)))
        );

        setCurrentStage(Conversations.createStage(this, "Welche Art von Loot-Objekt möchtest du erstellen?",
                Answer.of("Öffentlich - Alle Spieler teilen sich den Loot.", (type, config) -> lootObject.setPublicLootObject(true), Action.changeStage(cooldownStage)),
                Answer.of("Privat - Jeder Spieler kann unabhängig looten.", (type, config) -> lootObject.setPublicLootObject(false), Action.changeStage(cooldownStage)),
                Answer.of("Unendlich - Jeder kann unendlich oft daraus looten.", (type, config) -> lootObject.setInfinite(true), Action.changeStage(cooldownStage))));

        return true;
    }

    @Override
    protected void onEnd(ConversationEndReason reason) {

        lootObject.save();
    }
}
