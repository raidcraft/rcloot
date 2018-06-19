package de.raidcraft.loot.database.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.LootTableManager;
import de.raidcraft.loot.api.object.*;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.exceptions.NoLinkedRewardTableException;
import de.raidcraft.loot.util.TreasureRewardLevel;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (\n" +
                            "`id` INT NOT NULL AUTO_INCREMENT ,\n" +
                            "`loot_table_id` INT( 11 ) NOT NULL ,\n" +
                            "`world` VARCHAR ( 32 ) NOT NULL ,\n" +
                            "`x` INT( 11 ) NOT NULL ,\n" +
                            "`y` INT( 11 ) NOT NULL ,\n" +
                            "`z` INT( 11 ) NOT NULL ,\n" +
                            "`cooldown` INT( 11 ) NOT NULL ,\n" +
                            "`creator` VARCHAR ( 40 ) NOT NULL ,\n" +
                            "`creator_id` VARCHAR ( 40 ) NOT NULL ,\n" +
                            "`created` BIGINT( 20 ) NOT NULL , \n" +
                            "`enabled` TINYINT( 1 ) DEFAULT 1,\n" +
                            "`reward_level` INT( 11 ) DEFAULT 0,\n" +
                            "`public_chest` TINYINT( 1 ) DEFAULT 0, " +
                            "PRIMARY KEY ( `id` )\n" +
                            ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<LootObject> getAllObjects() {

        List<LootObject> lootObjects = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + ";");

            while (resultSet.next()) {
                int cooldown = resultSet.getInt("cooldown");
                int rewardLevel = resultSet.getInt("reward_level");
                boolean publicChest = resultSet.getBoolean("public_chest");

                LootObject lootObject;
                if (cooldown != -1) {
                    if (publicChest) {
                        lootObject = new SimplePublicLootObject();
                        ((SimplePublicLootObject) lootObject).setCooldown(cooldown);
                    } else {
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
                lootObject.setCreator(UUID.fromString(resultSet.getString("creator_id")));
                lootObject.setCreated(resultSet.getLong("created"));
                lootObject.setEnabled(resultSet.getBoolean("enabled"));

                World world = Bukkit.getWorld(resultSet.getString("world"));
                if (world == null) {
                    continue;
                }
                LootTableManager lootTableManager = RaidCraft.getComponent(LootPlugin.class).getLootTableManager();
                Location hostLocation = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                lootObject.setHostLocation(hostLocation);
                if (lootObject instanceof TreasureLootObject) {
                    try {
                        lootObject.assignLootTable(lootTableManager.getTable(TreasureRewardLevel.getLinkedTable(rewardLevel)));
                    } catch (NoLinkedRewardTableException e) {
                        RaidCraft.LOGGER.warning("[Loot] Try to load treasure object: " + e.getMessage());
                        continue;
                    }
                } else {
                    lootObject.assignLootTable(lootTableManager.getTable(resultSet.getInt("loot_table_id")));
                }
                lootObjects.add(lootObject);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lootObjects;
    }

    public List<LootObject> getAllObjectsByChunk(Chunk chunk) {

        List<LootObject> lootObjects = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM " + getTableName() + " WHERE " +
                            "world = '" + chunk.getWorld().getName() + "' AND " +
                            "x >= '" + (chunk.getX() * 16) + "' AND " +
                            "x < '" + (chunk.getX() * 16 + 16) + "' AND " +
                            "z >= '" + (chunk.getZ() * 16) + "' AND " +
                            "z < '" + (chunk.getZ() * 16 + 16) + "'");

            while (resultSet.next()) {
                int cooldown = resultSet.getInt("cooldown");
                int rewardLevel = resultSet.getInt("reward_level");
                boolean publicChest = resultSet.getBoolean("public_chest");

                LootObject lootObject;
                if (cooldown != -1) {
                    if (publicChest) {
                        lootObject = new SimplePublicLootObject();
                        ((SimplePublicLootObject) lootObject).setCooldown(cooldown);
                    } else {
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
                lootObject.setCreator(UUID.fromString(resultSet.getString("creator_id")));
                lootObject.setCreated(resultSet.getLong("created"));
                lootObject.setEnabled(resultSet.getBoolean("enabled"));

                World world = Bukkit.getWorld(resultSet.getString("world"));
                if (world == null) {
                    continue;
                }
                LootTableManager lootTableManager = RaidCraft.getComponent(LootPlugin.class).getLootTableManager();
                Location hostLocation = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                lootObject.setHostLocation(hostLocation);
                if (lootObject instanceof TreasureLootObject) {
                    try {
                        lootObject.assignLootTable(lootTableManager.getTable(TreasureRewardLevel.getLinkedTable(rewardLevel)));
                    } catch (NoLinkedRewardTableException e) {
                        RaidCraft.LOGGER.warning("[Loot] Try to load treasure object: " + e.getMessage());
                        continue;
                    }
                } else {
                    lootObject.assignLootTable(lootTableManager.getTable(resultSet.getInt("loot_table_id")));
                }
                lootObjects.add(lootObject);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            object.getLootTable().save();
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
        if (object instanceof PublicLootObject) {
            publicChest = true;
        }

        try {
            String query = "INSERT INTO " + getTableName() + " (loot_table_id, world, x, y, z, cooldown, creator_id, created, enabled, reward_level, public_chest) " +
                    "VALUES (" +
                    "'" + object.getLootTable().getId() + "'" + "," +
                    "'" + object.getHostLocation().getWorld().getName() + "'" + "," +
                    "'" + object.getHostLocation().getBlockX() + "'" + "," +
                    "'" + object.getHostLocation().getBlockY() + "'" + "," +
                    "'" + object.getHostLocation().getBlockZ() + "'" + "," +
                    "'" + cooldown + "'" + "," +
                    "'" + object.getCreator().toString() + "'" + "," +
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
            statement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isNearLootObject(Location location, int radiusXZ, int radiusY) {

        try {
            ResultSet resultSet = executeQuery(
                    "SELECT * FROM `" + getTableName() + "` WHERE " +
                            "x > '" + (location.getBlockX() - radiusXZ) + "' AND " +
                            "x < '" + (location.getBlockX() + radiusXZ) + "' AND " +
                            "z > '" + (location.getBlockZ() - radiusXZ) + "' AND " +
                            "z < '" + (location.getBlockZ() + radiusXZ) + "' AND " +
                            "y > '" + (location.getBlockY() - radiusY) + "' AND " +
                            "y < '" + (location.getBlockY() + radiusY) + "'"
            );

            while (resultSet.next()) {
                resultSet.close();
                return true;
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteObject(LootObject object) {

        try {
            executeUpdate(
                    "DELETE FROM " + getTableName() + " WHERE id = '" + object.getId() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateObject(LootObject object) {

        if (object.getId() == 0) {
            return;
        }
    }
}
