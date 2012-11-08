package de.raidcraft.loot.database.tables;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.componentutils.database.Database;
import de.raidcraft.componentutils.database.Table;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.object.LootObject;
import de.raidcraft.loot.object.SimpleLootObject;
import de.raidcraft.loot.object.SimpleTimedLootObject;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

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
public class LootObjectsTable extends Table {

    public LootObjectsTable() {

        super("objects", LootDatabase.tablePrefix);
    }

    @Override
    public void createTable() {

        try {
            getConnection().prepareStatement(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`loot_table_id` INT( 11 ) NOT NULL ,\n" +
                            "`world` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`x` INT( 11 ) NOT NULL ,\n" +
                            "`y` INT( 11 ) NOT NULL ,\n" +
                            "`z` INT( 11 ) NOT NULL ,\n" +
                            "`cooldown` INT( 11 ) NOT NULL ,\n" +
                            "`creator` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`created` BIGINT( 20 ) NOT NULL , \n" +
                            "`enabled` TINYINT( 1 ) DEFAULT 1,\n" +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
    }

    public List<LootObject> getAllObjects() {

        List<LootObject> lootObjects = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + ";").executeQuery();

            while (resultSet.next()) {
                int cooldown = resultSet.getInt("cooldown");

                LootObject lootObject;
                if (cooldown != -1) {
                    lootObject = new SimpleTimedLootObject();
                    ((SimpleTimedLootObject) lootObject).setCooldown(cooldown);
                } else {
                    lootObject = new SimpleLootObject();
                }
                lootObject.setId(resultSet.getInt("id"));
                lootObject.setCreator(resultSet.getString("creator"));
                lootObject.setEnabled(resultSet.getBoolean("enabled"));

                Block host = Bukkit.getWorld(resultSet.getString("world")).getBlockAt(resultSet.getInt("x")
                        , resultSet.getInt("y"), resultSet.getInt("z"));
                lootObject.setHost(host);
                lootObject.assignLootTable(Database.getTable(LootTablesTable.class).getLootTable(resultSet.getInt("loot_table_id")));

                lootObjects.add(lootObject);
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
        }
        return lootObjects;
    }

    public void addObject(LootObject object) {

        // return if object already saved
        if (object.getId() != 0) {
            return;
        }

        // save table if not done yes
        if (object.getLootTable().getId() == 0) {
            Database.getTable(LootTablesTable.class).addLootTable(object.getLootTable());
        }

        int cooldown = -1;
        if (object instanceof SimpleTimedLootObject) {
            cooldown = ((SimpleTimedLootObject) object).getCooldown();
        }
        try {
            String query = "INSERT INTO " + getTableName() + " (loot_table_id, world, x, y, z, cooldown, creator, created, enabled) " +
                    "VALUES (" +
                    "'" + object.getLootTable().getId() + "'" + "," +
                    "'" + object.getHost().getLocation().getWorld().getName() + "'" + "," +
                    "'" + object.getHost().getLocation().getBlockX() + "'" + "," +
                    "'" + object.getHost().getLocation().getBlockY() + "'" + "," +
                    "'" + object.getHost().getLocation().getBlockZ() + "'" + "," +
                    "'" + cooldown + "'" + "," +
                    "'" + object.getCreator() + "'" + "," +
                    "'" + object.getCreated() + "'" + "," +
                    "'" + ((object.isEnabled()) ? 1 : 0) + "'" +
                    ");";

            Statement statement = getConnection().createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs != null && rs.next()) {
                object.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteObject(LootObject object) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE id = '" + object.getId() + "';").execute();
        } catch (SQLException e) {
            CommandBook.logger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateObject(LootObject object) {

        if (object.getId() == 0) {
            return;
        }
    }
}
