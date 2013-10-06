package de.raidcraft.loot.database.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.table.SimpleLootTableEntry;
import de.raidcraft.util.ItemUtils;
import de.raidcraft.util.SerializationUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`loot_table_id` INT( 11 ) NOT NULL ,\n" +
                            "`item` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`durability` INT( 11 ) NOT NULL ,\n" +
                            "`amount` INT( 11 ) NOT NULL , \n" +
                            "`itemdata` TEXT NOT NULL , \n" +
                            "`chance` INT( 11 ) NOT NULL , \n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEntries(LootTable table) {

        for (LootTableEntry entry : table.getEntries()) {
            String itemData;
            itemData = SerializationUtil.toByteStream(entry.getItem().getItemMeta());

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

                    Statement statement = getConnection().createStatement();
                    statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs != null && rs.next()) {
                        entry.setId(rs.getInt(1));
                    }
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<LootTableEntry> getEntries(LootTable lootTable) {

        List<LootTableEntry> entries = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE loot_table_id = '" + lootTable.getId() + "';");

            while (resultSet.next()) {
                ItemStack itemStack = new ItemStack(
                        Material.getMaterial(resultSet.getString("item")),
                        resultSet.getInt("amount"),
                        resultSet.getShort("durability")
                );

                String itemData = resultSet.getString("itemdata");

//              convert old serialized item meta
                int id = resultSet.getInt("id");
                if(itemData == null || itemData.length() == 0 || itemData.contains("|")) {
                    ItemUtils.Serialization serialization = new ItemUtils.Serialization(itemStack);
                    ItemStack oldItem = serialization.getDeserializedItem(itemData);
                    itemData = SerializationUtil.toByteStream(oldItem.getItemMeta());
                    executeUpdate(
                            "UPDATE " + getTableName() + " SET itemdata = '" + itemData + "' " +
                                    "WHERE id = '" + id + "';");
                }

                itemStack.setItemMeta((ItemMeta)SerializationUtil.fromByteStream(itemData, itemStack.getType()));

                SimpleLootTableEntry entry = new SimpleLootTableEntry();
                entry.setId(id);
                entry.setChance(resultSet.getInt("chance"));
                entry.setItem(itemStack);
                entries.add(entry);

                // this converts the entry into an raidcraft conform item id string
                String itemIdString = RaidCraft.getItemIdString(itemStack);
                executeUpdate("UPDATE " + getTableName() + " SET item_id_string='" + itemIdString + "' WHERE id=" + id + ";");
            }
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    public void deleteEntries(LootTable table) {

        try {
            executeUpdate(
                    "DELETE FROM " + getTableName() + " WHERE loot_table_id = '" + table.getId() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
