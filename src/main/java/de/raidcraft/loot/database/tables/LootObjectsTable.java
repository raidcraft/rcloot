package de.raidcraft.loot.database.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.exceptions.NoLinkedRewardTableException;
import de.raidcraft.loot.object.*;
import de.raidcraft.loot.util.TreasureRewardLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
                            "`reward_level` INT( 11 ) DEFAULT 0,\n" +
                            "`public_chest` TINYINT( 1 ) DEFAULT 0, " +
                            "PRIMARY KEY ( `id` )\n" +
                            ")").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
    }

    public List<LootObject> getAllObjects() {

        List<LootObject> lootObjects = new ArrayList<>();
        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM " + getTableName() + ";").executeQuery();

            while (resultSet.next()) {
                int cooldown = resultSet.getInt("cooldown");
                int rewardLevel = resultSet.getInt("reward_level");
                boolean publicChest = resultSet.getBoolean("public_chest");

                LootObject lootObject;
                if (cooldown != -1) {
                    if(publicChest) {
                        lootObject = new SimplePublicLootObject();
                        ((SimplePublicLootObject) lootObject).setCooldown(cooldown);
                    }
                    else {
                        lootObject = new SimpleTimedLootObject();
                        ((SimpleTimedLootObject) lootObject).setCooldown(cooldown);
                    }
                } else {
                    lootObject = new SimpleLootObject();
                }
                if (rewardLevel > 0) {
                    lootObject = new SimpleTreasureLootObject();
                    ((TreasureLootObject) lootObject).setRewardLevel(rewardLevel);
                }
                lootObject.setId(resultSet.getInt("id"));
                lootObject.setCreator(resultSet.getString("creator"));
                lootObject.setCreated(resultSet.getLong("created"));
                lootObject.setEnabled(resultSet.getBoolean("enabled"));

                World world = Bukkit.getWorld(resultSet.getString("world"));
                if (world == null) {
                    continue;
                }
                Block host = world.getBlockAt(resultSet.getInt("x")
                        , resultSet.getInt("y"), resultSet.getInt("z"));
                lootObject.setHost(host);
                if (lootObject instanceof TreasureLootObject) {
                    try {
                        lootObject.assignLootTable(RaidCraft.getTable(LootTablesTable.class)
                                .getLootTable(TreasureRewardLevel.getLinkedTable(rewardLevel)));
                    } catch (NoLinkedRewardTableException e) {
                        RaidCraft.LOGGER.warning("[Loot] Try to load treasure object: " + e.getMessage());
                        continue;
                    }
                } else {
                    lootObject.assignLootTable(RaidCraft.getTable(LootTablesTable.class).getLootTable(resultSet.getInt("loot_table_id")));
                }
                lootObjects.add(lootObject);
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return lootObjects;
    }

    public void addObject(LootObject object) {

        // return if object already saved
        if (object.getId() != 0) {
            return;
        }

        // save table if not done yet
        if (object.getLootTable().getId() == 0) {
            RaidCraft.getTable(LootTablesTable.class).addLootTable(object.getLootTable());
        }

        int cooldown = -1;
        int rewardLevel = 0;
        boolean publicChest = false;

        if (object instanceof TimedLootObject) {
            cooldown = ((TimedLootObject) object).getCooldown();
        }
        if (object instanceof TreasureLootObject) {
            rewardLevel = ((TreasureLootObject) object).getRewardLevel();
        }
        if(object instanceof PublicLootObject) {
            publicChest = true;
        }

        try {
            String query = "INSERT INTO " + getTableName() + " (loot_table_id, world, x, y, z, cooldown, creator, created, enabled, reward_level, public_chest) " +
                    "VALUES (" +
                    "'" + object.getLootTable().getId() + "'" + "," +
                    "'" + object.getHost().getLocation().getWorld().getName() + "'" + "," +
                    "'" + object.getHost().getLocation().getBlockX() + "'" + "," +
                    "'" + object.getHost().getLocation().getBlockY() + "'" + "," +
                    "'" + object.getHost().getLocation().getBlockZ() + "'" + "," +
                    "'" + cooldown + "'" + "," +
                    "'" + object.getCreator() + "'" + "," +
                    "'" + object.getCreated() + "'" + "," +
                    "'" + ((object.isEnabled()) ? 1 : 0) + "'" + "," +
                    "'" + rewardLevel + "'" + "," +
                    "'" + ((publicChest) ? 1 : 0) + "'" +
                    ");";

            Statement statement = getConnection().createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs != null && rs.next()) {
                object.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isNearLootObject(Location location, int radiusXZ, int radiusY) {

        try {
            ResultSet resultSet = getConnection().prepareStatement(
                    "SELECT * FROM `" + getTableName() + "` WHERE " +
                    "x > '" + (location.getBlockX() - radiusXZ) + "' AND " +
                    "x < '" + (location.getBlockX() + radiusXZ) + "' AND " +
                    "z > '" + (location.getBlockZ() - radiusXZ) + "' AND " +
                    "z < '" + (location.getBlockZ() + radiusXZ) + "' AND " +
                    "y > '" + (location.getBlockY() - radiusY) + "' AND " +
                    "y < '" + (location.getBlockY() + radiusY) + "'"
            ).executeQuery();

            while (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return false;
    }

    public void deleteObject(LootObject object) {

        try {
            getConnection().prepareStatement(
                    "DELETE FROM " + getTableName() + " WHERE id = '" + object.getId() + "';").execute();
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateObject(LootObject object) {

        if (object.getId() == 0) {
            return;
        }
    }
}
