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
    private int amount = 1;
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

    public int getAmount() {

        return amount;
    }

    public void setAmount(int amount) {

        this.amount = amount;
    }

    public double getChance() {

        return chance;
    }

    public void setChance(double chance) {

        this.chance = chance;
    }
}