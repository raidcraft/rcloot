package de.raidcraft.loot.database.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableManager;
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
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`min_loot` INT( 11 ) NOT NULL ,\n" +
                            "`max_loot` INT( 11 ) NOT NULL ,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")");
        } catch (SQLException e) {
            e.printStackTrace();
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
            RaidCraft.getTable(LootTableEntriesTable.class).addEntries(table);
            statement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LootTable getLootTable(int id) {

        // check first if table already cached
        LootTable table = RaidCraft.getComponent(LootPlugin.class).getLootTableCache().getTable(id);
        if(table != null) {
            return table;
        }
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE id = '" + id + "';");

            while (resultSet.next()) {
                table = new SimpleLootTable();
                table.setId(resultSet.getInt("id"));
                table.setMinLootItems(resultSet.getInt("min_loot"));
                table.setMaxLootItems(resultSet.getInt("max_loot"));
                table.setEntries(RaidCraft.getTable(LootTableEntriesTable.class).getEntries(table));
                resultSet.close();
                // cache new table
                RaidCraft.getComponent(LootPlugin.class).getLootTableCache().addTable(table);
                return table;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<LootTable> getAllLootTables() {

        LootTableManager cache = RaidCraft.getComponent(LootPlugin.class).getLootTableCache();
        List<LootTable> tables = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE id NOT IN(" + cache.getIdStringList() + ");");

            while (resultSet.next()) {
                SimpleLootTable table = new SimpleLootTable();
                table.setId(resultSet.getInt("id"));
                table.setMinLootItems(resultSet.getInt("min_loot"));
                table.setMaxLootItems(resultSet.getInt("max_loot"));
                table.setEntries(RaidCraft.getTable(LootTableEntriesTable.class).getEntries(table));
                tables.add(table);
                // cache new table
                RaidCraft.getComponent(LootPlugin.class).getLootTableCache().addTable(table);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public void deleteTable(LootTable table) {

        try {
            executeUpdate(
                    "DELETE FROM " + getTableName() + " WHERE id = '" + table.getId() + "';");
            RaidCraft.getTable(LootTableEntriesTable.class).deleteEntries(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
