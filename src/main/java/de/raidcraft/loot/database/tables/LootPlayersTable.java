package de.raidcraft.loot.database.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.componentutils.database.Table;
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
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`object_id` INT( 11 ) NOT NULL ,\n" +
                            "`player` VARCHAR( 32 ) NOT NULL ,\n" +
                            "`timestamp` BIGINT( 20 ) NOT NULL , \n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }
    
    public void addEntry(String player, int objectId, long timestamp) {
        try {
            getConnection().prepareStatement(
                    "INSERT INTO " + getTableName() + " (object_id, player, timestamp) " +
                            "VALUES (" +
                            "'" + objectId + "'" + "," +
                            "'" + player + "'" + "," +
                            "'" + timestamp + "'" +
                            ");"
            ).execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public boolean hasLooted(String player, int objectId) {
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' AND object_id = '" + objectId + "';").executeQuery();

            while (resultSet.next()) {
                return true;
            }
        }
        catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return false;
    }
    
    public long getLastLooted(String player, int objectId) {
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + " WHERE player = '" + player + "' AND object_id = '" + objectId + "' ORDER BY timestamp DESC;").executeQuery();

            while (resultSet.next()) {
                    return resultSet.getLong("timestamp");
            }
        }
        catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return 0;
    }
}
