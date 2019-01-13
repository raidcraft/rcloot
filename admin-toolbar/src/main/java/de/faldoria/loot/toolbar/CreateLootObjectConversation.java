package de.faldoria.loot.toolbar;

import de.faldoria.loot.api.LootObject;
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
import de.raidcraft.util.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Data
@EqualsAndHashCode(callSuper = true)
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


        InputAnswer inputAnswer = new InputAnswer("Cooldown (Respawnzeit für zerstörbare Objekte) in Sekunden (-1 für keinen Cooldown/Respawn).")
                .setInputListener(input -> lootObject.setCooldown(Integer.parseInt(input)));
        Stage cooldownStage = Conversations.createStage(this, "Was für einen Cooldown/Respawn soll das Loot-Objekt haben?",
                inputAnswer
        );
        inputAnswer.addActionToAnswer(Action.endConversation(ConversationEndReason.ENDED));
        addStage(cooldownStage.getTemplate());

        setCurrentStage(Conversations.createStage(this, "Welche Art von Loot-Objekt möchtest du erstellen?",
                Answer.of("Öffentlich - Alle Spieler teilen sich den Loot, Cooldown, etc..", (type, config) -> lootObject.setPublicLootObject(true), Action.changeStage(cooldownStage)),
                Answer.of("Privat - Jeder Spieler kann unabhängig looten.", (type, config) -> lootObject.setPublicLootObject(false), Action.changeStage(cooldownStage)),
                Answer.of("Unendlich - Jeder kann unendlich oft daraus looten.", (type, config) -> lootObject.setInfinite(true), Action.changeStage(cooldownStage)),
                Answer.of("Zerstörbar - Der Block wird nach dem Looten zerstört.", (type, config) -> lootObject.setDestroyable(true), Action.changeStage(cooldownStage)),
                Answer.of("Standard Werte übernehmen: einmalig für jeden Spieler lootbar.", (type, config) -> lootObject.toString(), Action.endConversation(ConversationEndReason.ENDED)),
                lastConfig != null ? Answer.of("Konfiguration des letzten Loot-Objekts übernehmen. "
                        + (lastConfig.isPublicLootObject() ? (lastConfig.isDestroyable() ? ChatColor.RED + "Zerstörbar" : ChatColor.GREEN + "Öffentlich") : ChatColor.DARK_PURPLE + "Privat")
                        + ChatColor.GOLD + " - "
                        + (lastConfig.isInfinite() ? ChatColor.AQUA + "Unendlich" : ChatColor.AQUA + "Endlich")
                        + ChatColor.GOLD + " - "
                        + ChatColor.GRAY + (lastConfig.isDestroyable() ? "Respawnzeit: " : "Cooldown: ") + TimeUtil.getFormattedTime(lastConfig.getCooldown()), (type, config) -> {
                    lootObject.setDestroyable(lastConfig.isDestroyable());
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
