package de.raidcraft.loot.database.tables;

import de.raidcraft.api.database.Table;
import de.raidcraft.loot.database.LootDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author: Philip
 * Date: 16.10.12 - 19:49
 * Description:
 */
public class LootPlayersTable extends Table {

    public LootPlayersTable() {

        super("players", LootDatabase.tablePrefix);
    }

    @Override
    public void createTable() {

        try {
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`object_id` INT( 11 ) NOT NULL ,\n" +
                            "`player` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`timestamp` BIGINT( 20 ) NOT NULL , \n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEntry(String player, int objectId, long timestamp) {

        try {
            executeUpdate(
                    "INSERT INTO " + getTableName() + " (object_id, player, timestamp) " +
                            "VALUES (" +
                            "'" + objectId + "'" + "," +
                            "'" + player + "'" + "," +
                            "'" + timestamp + "'" +
                            ");"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLooted(String player, int objectId) {

        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' AND object_id = '" + objectId + "';");

            while (resultSet.next()) {
                resultSet.close();
                return true;
            }
            resultSet.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public long getLastLooted(String player, int objectId) {

        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' AND object_id = '" + objectId + "' ORDER BY timestamp DESC;");

            while (resultSet.next()) {
                resultSet.close();
                return resultSet.getLong("timestamp");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
        return 0;
    }
}
