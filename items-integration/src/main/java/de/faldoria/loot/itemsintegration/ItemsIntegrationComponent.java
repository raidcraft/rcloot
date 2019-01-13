package de.faldoria.loot.itemsintegration;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.components.BukkitComponent;
import de.raidcraft.api.components.ComponentInformation;
import de.raidcraft.api.components.Depend;
import de.raidcraft.api.random.Dropable;
import de.raidcraft.api.random.RDS;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.api.random.RDSTable;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@ComponentInformation(
        value = "items",
        friendlyName = "Items Integration",
        desc = "Integrates RCItems level dependent loot tables."
)
@Depend(plugins = {"RCItems"})
public class ItemsIntegrationComponent extends BukkitComponent {

    private final Map<String, Map<Integer, RDSTable>> levelDependantTables = new CaseInsensitiveMap<>();

    @Override
    public void enable() {
        RDS.registerObject(new LevelDependantLootTable.Factory());
        registerCommands(LootCommands.class);
    }

    @Override
    public void disable() {
        RDS.unregisterObject(new LevelDependantLootTable.Factory());
        levelDependantTables.clear();
    }

    @Override
    public void reload() {
        levelDependantTables.clear();
    }

    public RDSTable getLevelDependantLootTable(String name, int level) {

        if (levelDependantTables.containsKey(name) && levelDependantTables.get(name).containsKey(level)) {
            return levelDependantTables.get(name).get(level);
        }
        Optional<RDSTable> table = RDS.getTable(name);
        if (table.isPresent() && table.get() instanceof LevelDependantLootTable) {
            if (!levelDependantTables.containsKey(name)) {
                levelDependantTables.put(name, new HashMap<>());
            }
            LevelDependantLootTable lootTable = ((LevelDependantLootTable) table.get()).createInstance();
            levelDependantTables.get(name).put(level, lootTable);
            updateLevelDependantTables(lootTable, level);
            return lootTable;
        }
        if (table.isPresent()) {
            updateLevelDependantTables(table.get(), level);
            return table.get();
        }
        return null;
    }

    private void updateLevelDependantTables(RDSTable table, int level) {

        if (table instanceof LevelDependantLootTable) {
            ((LevelDependantLootTable) table).setLevel(level);
            ((LevelDependantLootTable) table).loadItems();
        }
        for (RDSObject object : table.getContents()) {
            if (object instanceof LevelDependantLootTable) {
                ((LevelDependantLootTable) object).setLevel(level);
                ((LevelDependantLootTable) object).loadItems();
            } else if (object instanceof RDSTable) {
                updateLevelDependantTables((RDSTable) object, level);
            }
        }
    }

    public class LootCommands {

        @Command(
                aliases = {"simulate-loot"},
                desc = "Simulates the looting of the given loot table",
                usage = "<table> <level>",
                min = 1
        )
        @CommandPermissions("loot.simulate")
        public void simulate(CommandContext args, CommandSender sender) throws CommandException {

            RDSTable table = getLevelDependantLootTable(args.getString(0), args.getInteger(1, 1));
            if (table == null) {
                throw new CommandException("The loot table " + args.getString(0) + " does not exist!");
            }
            int count = 0;
            Collection<RDSObject> result = new ArrayList<>();
            for (int i = 0; i < getConfiguration().getInt("simulation-count"); i++) {
                result = table.loot((Player) sender);
                if (!result.isEmpty()) {
                    count = i;
                    break;
                }
            }

            if (result.isEmpty()) {
                throw new CommandException("Could not get a valid result after 100 iterations! Is the loot table configured correctly?");
            }

            Inventory inventory = Bukkit.createInventory((Player) sender, 54);
            result.stream().filter(rdsObject -> rdsObject instanceof Dropable).forEach(rdsObject -> {
                ItemStack itemStack = ((Dropable) rdsObject).getItemStack();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = itemMeta.getLore();
                lore.add("Chance: " + rdsObject.getProbability());
                lore.add("Source: " + rdsObject.getTable());
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                inventory.addItem(itemStack);
            });
            ((Player) sender).openInventory(inventory);
            sender.sendMessage(ChatColor.GREEN + "Looted " + (count + 1) + "x to get any loot.");
        }
    }
}
