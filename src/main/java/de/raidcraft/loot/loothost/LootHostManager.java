package de.raidcraft.loot.loothost;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.loot.LootPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class LootHostManager implements Component {

    private LootPlugin plugin;
    private Map<Material, LootHost> registeredHosts = new HashMap<>();

    public LootHostManager(LootPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(LootHostManager.class, this);
    }

    public void registerLootHost(LootHost lootHost) {

        registeredHosts.put(lootHost.getMaterial(), lootHost);
    }

    public LootHost getLootHost(Material material) {

        return registeredHosts.get(material);
    }

    public LootHost getLootHost(Block block) {
        if (block == null) return null;
        return getLootHost(block.getType());
    }

    public LootHost getLootHost(Inventory inventory) {

        if (inventory == null) return null;

        for (LootHost lootHost : registeredHosts.values()) {

            if (lootHost.validateInventory(inventory)) {
                return lootHost;
            }
        }
        return null;
    }
}
