package de.raidcraft.loot.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_entries")
public class TLootTableEntry {

    @Id
    private int id;
    @ManyToOne
    @NotNull
    @Column(name = "loot_table_id")
    private TLootTable lootTable;
    @NotNull
    private String item;
    private int minAmount = 1;
    private int maxAmount = minAmount;
    private double chance = 1.0;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TLootTable getLootTable() {

        return lootTable;
    }

    public void setLootTable(TLootTable lootTable) {

        this.lootTable = lootTable;
    }

    public String getItem() {

        return item;
    }

    public void setItem(String item) {

        this.item = item;
    }

    public int getMinAmount() {

        return minAmount;
    }

    public void setMinAmount(int minAmount) {

        this.minAmount = minAmount;
    }

    public int getMaxAmount() {

        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {

        this.maxAmount = maxAmount;
    }

    public double getChance() {

        return chance;
    }

    public void setChance(double chance) {

        this.chance = chance;
    }
}
