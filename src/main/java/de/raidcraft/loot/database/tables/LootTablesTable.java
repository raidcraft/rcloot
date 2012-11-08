package de.raidcraft.loot.database.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.componentutils.database.Database;
import de.raidcraft.componentutils.database.Table;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.SimpleLootTable;

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
public class LootTablesTable extends Table {

    public LootTablesTable() {

        super("tables", LootDatabase.tablePrefix);
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`min_loot` INT( 11 ) NOT NULL ,\n" +
                            "`max_loot` INT( 11 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    // add new loot table and save entries
    public void addLootTable(LootTable table) {
        // return if table already saved
        if (table.getId() != 0) {
            return;
        }

        try {
            String query = "INSERT INTO " + getTableName() + " (min_loot, max_loot) " +
                    "VALUES (" +
                    "'" + table.getMinLootItems() + "'" + "," +
                    "'" + table.getMaxLootItems() + "'" +
                    ");";
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs != null && rs.next()) {
                table.setId(rs.getInt(1));
            }
            Database.getTable(LootTableEntriesTable.class).addEntries(table);

        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public LootTable getLootTable(int id) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE id = '" + id + "';").executeQuery();

            while (resultSet.next()) {
                SimpleLootTable table = new SimpleLootTable();
                table.setId(resultSet.getInt("id"));
                table.setMinLootItems(resultSet.getInt("min_loot"));
                table.setMaxLootItems(resultSet.getInt("max_loot"));
                table.setEntries(Database.getTable(LootTableEntriesTable.class).getEntries(table));
                return table;
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return null;
    }

    public List<LootTable> getAllLootTables() {

        List<LootTable> tables = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + ";").executeQuery();

            while (resultSet.next()) {
                SimpleLootTable table = new SimpleLootTable();
                table.setId(resultSet.getInt("id"));
                table.setMinLootItems(resultSet.getInt("min_loot"));
                table.setMaxLootItems(resultSet.getInt("max_loot"));
                table.setEntries(Database.getTable(LootTableEntriesTable.class).getEntries(table));
                tables.add(table);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return tables;
    }

    public void deleteTable(LootTable table) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE id = '" + table.getId() + "';").execute();
            Database.getTable(LootTableEntriesTable.class).deleteEntries(table);
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
