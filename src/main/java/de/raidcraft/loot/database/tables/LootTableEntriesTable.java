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
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`loot_table_id` INT( 11 ) NOT NULL ,\n" +
                            "`item` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`durability` INT( 11 ) NOT NULL ,\n" +
                            "`amount` INT( 11 ) NOT NULL , \n" +
                            "`itemdata` TEXT NOT NULL , \n" +
                            "`chance` INT( 11 ) NOT NULL , \n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public void addEntries(LootTable table) {

        for (LootTableEntry entry : table.getEntries()) {
            String itemData;
            itemData = SerializationUtil.toByteStream_fixed(entry.getItem().getItemMeta());

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
                } catch (SQLException e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public List<LootTableEntry> getEntries(LootTable lootTable) {

        List<LootTableEntry> entries = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE loot_table_id = '" + lootTable.getId() + "';").executeQuery();

            while (resultSet.next()) {
                ItemStack itemStack = new ItemStack(
                        Material.getMaterial(resultSet.getString("item")),
                        resultSet.getInt("amount"),
                        resultSet.getShort("durability")
                );

                String itemData = resultSet.getString("itemdata");

//              convert old serialized item meta
                if(itemData == null || itemData.length() == 0 || itemData.contains("|")) {
                    ItemUtils.Serialization serialization = new ItemUtils.Serialization(itemStack);
                    ItemStack oldItem = serialization.getDeserializedItem(itemData);
                    itemData = SerializationUtil.toByteStream_fixed(oldItem.getItemMeta());
                    getConnection().prepareStatement(
                            "UPDATE " + getTableName() + " SET itemdata = '" + itemData + "' " +
                                    "WHERE id = '" + resultSet.getInt("id") + "';").executeUpdate();
                }

                itemStack.setItemMeta((ItemMeta)SerializationUtil.fromByteStream(itemData, itemStack.getType()));

                SimpleLootTableEntry entry = new SimpleLootTableEntry();
                entry.setId(resultSet.getInt("id"));
                entry.setChance(resultSet.getInt("chance"));
                entry.setItem(itemStack);
                entries.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    public void deleteEntries(LootTable table) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE loot_table_id = '" + table.getId() + "';").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
