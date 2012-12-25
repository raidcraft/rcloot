package de.raidcraft.loot.database.tables;

import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.loot.LootModule;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.table.SimpleLootTableEntry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 16.10.12 - 19:48
 * Description:
 */
public class LootTableEntriesTable extends Table {

    public LootTableEntriesTable() {

        super("entries", LootDatabase.tablePrefix);
    }

    @Override
    public void createTable() {

        try {
            LootModule.INST.getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`loot_table_id` INT( 11 ) NOT NULL ,\n" +
                            "`item` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`durability` INT( 11 ) NOT NULL ,\n" +
                            "`amount` INT( 11 ) NOT NULL , \n" +
                            "`itemdata` TEXT( 300 ) NOT NULL , \n" +
                            "`chance` INT( 11 ) NOT NULL , \n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public void addEntries(LootTable table) {

        for (LootTableEntry entry : table.getEntries()) {
            String itemData = getItemData(entry.getItem());
            // save entry if doesn't save yet
            if (entry.getId() == 0) {
                try {
                    String query = "INSERT INTO " + getTableName() + " (item, loot_table_id, durability, amount, itemdata, chance) " +
                            "VALUES (" +
                            "'" + entry.getItem().getType().name() + "'" + "," +
                            "'" + table.getId() + "'" + "," +
                            "'" + entry.getItem().getDurability() + "'" + "," +
                            "'" + entry.getItem().getAmount() + "'" + "," +
                            "'" + itemData + "'" + "," +
                            "'" + entry.getChance() + "'" +
                            ");";

                    Statement statement = LootModule.INST.getConnection().createStatement();
                    statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs != null && rs.next()) {
                        entry.setId(rs.getInt(1));
                    }
                } catch (SQLException e) {
                    CommandBook.logger().warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public List<LootTableEntry> getEntries(LootTable lootTable) {

        List<LootTableEntry> entries = new ArrayList<>();
        try {
            ResultSet resultSet = LootModule.INST.getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE loot_table_id = '" + lootTable.getId() + "';").executeQuery();

            while (resultSet.next()) {
                ItemStack itemStack = new ItemStack(
                        Material.getMaterial(resultSet.getString("item")),
                        resultSet.getInt("amount"),
                        resultSet.getShort("durability")
                );

                itemStack = addItemData(itemStack, resultSet.getString("itemdata"));

                SimpleLootTableEntry entry = new SimpleLootTableEntry();
                entry.setId(resultSet.getInt("id"));
                entry.setChance(resultSet.getInt("chance"));
                entry.setItem(itemStack);
                entries.add(entry);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return entries;
    }

    public void deleteEntries(LootTable table) {

        try {
            LootModule.INST.getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE loot_table_id = '" + table.getId() + "';").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    private String getItemData(ItemStack item) {
        if(item.getType() == Material.FIREWORK) {
            //TODO save firework rocket type
            return "";
        }
        else if(item.getType() == Material.BOOK_AND_QUILL) {
            //TODO save written books
            return "";
        }
        else {
            return getEnchantmentString(item.getItemMeta().getEnchants());
        }
    }
    
    private ItemStack addItemData(ItemStack itemstack, String data) {
        if(itemstack.getType() == Material.FIREWORK) {

        }
        else if(itemstack.getType() == Material.BOOK_AND_QUILL) {
            
        }
        else {
            itemstack.addEnchantments(getEnchantmentsByString(data));
        }
        return itemstack;
    }
    
    private String getEnchantmentString(Map<Enchantment, Integer> enchantments) {

        String enchantmentString = "";
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            enchantmentString += enchantment.getKey().getName() + ":" + enchantment.getValue() + "|";
        }
        return enchantmentString;
    }

    private Map<Enchantment, Integer> getEnchantmentsByString(String enchantmentString) {

        Map<Enchantment, Integer> enchantments = new HashMap<>();
        String[] enchantmentPairs = enchantmentString.split("|");
        for (String enchantmentPair : enchantmentPairs) {
            String[] enchantmentPairSplit = enchantmentPair.split(":");
            try {
                enchantments.put(Enchantment.getByName(enchantmentPairSplit[0]), Integer.parseInt(enchantmentPairSplit[1]));
            } catch (Exception e) {
            }
        }
        return enchantments;
    }
}
