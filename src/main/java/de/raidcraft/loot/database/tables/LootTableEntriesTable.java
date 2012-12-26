package de.raidcraft.loot.database.tables;

import com.silthus.raidcraft.util.component.database.Table;
import com.sk89q.commandbook.CommandBook;
import de.raidcraft.loot.LootModule;
import de.raidcraft.loot.database.LootDatabase;
import de.raidcraft.loot.table.LootTable;
import de.raidcraft.loot.table.LootTableEntry;
import de.raidcraft.loot.table.SimpleLootTableEntry;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

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

                addItemData(itemStack, resultSet.getString("itemdata"));

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
            return getFireworkEffectString(((FireworkMeta)item.getItemMeta()));
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
            addFireworkEffectMetaByString(data, itemstack);
        }
        else if(itemstack.getType() == Material.BOOK_AND_QUILL) {
            //TODO add book data
        }
        else {
            itemstack.addEnchantments(getEnchantmentsByString(data));
        }
        return itemstack;
    }
    
    /*
     * Enchantment conversation methods
     */
    private String getEnchantmentString(Map<Enchantment, Integer> enchantments) {

        String enchantmentString = "";
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            enchantmentString += enchantment.getKey().getName() + ":" + enchantment.getValue() + "|";
        }
        return enchantmentString;
    }

    private Map<Enchantment, Integer> getEnchantmentsByString(String enchantmentString) {

        Map<Enchantment, Integer> enchantments = new HashMap<>();
        String[] enchantmentPairs = enchantmentString.split("\\|");
        for (String enchantmentPair : enchantmentPairs) {
            String[] enchantmentPairSplit = enchantmentPair.split(":");
            try {
                enchantments.put(Enchantment.getByName(enchantmentPairSplit[0]), Integer.parseInt(enchantmentPairSplit[1]));
            } catch (Exception e) {
            }
        }
        return enchantments;
    }
    
    /*
     * Firework conversation methods
     * 
     * FireworkEffects structure: 
     * Power=EffectName:Flicker:Trail:Color1,Color2,...:FadeColor1,FadeColor2,...|EffectName2:...
     */
    private String getFireworkEffectString(FireworkMeta fireworkMeta) {
        
        String fireworkEffects = fireworkMeta.getPower() + "=";
        for (FireworkEffect effect : fireworkMeta.getEffects()) {
            fireworkEffects += effect.getType().name() + ":";
            fireworkEffects += ((effect.hasFlicker()) ? "1" : "0") + ":";
            fireworkEffects += ((effect.hasTrail()) ? "1" : "0") + ":";
            for(Color color : effect.getColors()) {
                fireworkEffects += color.asRGB() + ",";
            }
            fireworkEffects += ":";
            for(Color color : effect.getFadeColors()) {
                fireworkEffects += color.asRGB() + ",";
            }
            fireworkEffects += "|";
        }
        return fireworkEffects;
    }
    
    private void addFireworkEffectMetaByString(String fireworkEffectMetaString, ItemStack fireworkItem) {
        
        try {
            FireworkMeta fireworkMeta = (FireworkMeta)fireworkItem.getItemMeta();
            String[] powerPair = fireworkEffectMetaString.split("=");

            fireworkMeta.setPower(Integer.valueOf(powerPair[0]));
            
            String[] effects = powerPair[1].split("\\|");
            
            for(String effect : effects) {
                String[] effectParameter = effect.split(":");
                if(effectParameter.length < 4) continue;

                boolean flicker = (effectParameter[1] == "1") ? true : false;
                boolean trail = (effectParameter[2] == "1") ? true : false;
                
                String[] colorString = effectParameter[3].split(",");
                List<Color> colors = new ArrayList<>();
                for(String color : colorString) {
                    if(color.length() <= 0) continue;
                    colors.add(Color.fromRGB(Integer.valueOf(color)));
                }

                List<Color> fadeColors = new ArrayList<>();
                if(effectParameter.length > 4) {
                    String[] fadeColorString = effectParameter[4].split(",");
                    for(String color : fadeColorString) {
                        if(color.length() <= 0) continue;
                        fadeColors.add(Color.fromRGB(Integer.valueOf(color)));
                    }
                }
                
                FireworkEffect fireworkEffect = FireworkEffect.builder()
                        .with(FireworkEffect.Type.valueOf(effectParameter[0]))
                        .flicker(flicker)
                        .trail(trail)
                        .withColor(colors)
                        .withFade(fadeColors)
                        .build();

                fireworkMeta.addEffect(fireworkEffect);
            }
            fireworkItem.setItemMeta(fireworkMeta);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
