package de.raidcraft.loot.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.api.items.ItemQuality;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_qualities")
public class TLootTableQuality {

    @Id
    private int id;
    @NotNull
    @ManyToOne
    @Column(name = "loot_table_id")
    private TLootTable lootTable;
    @NotNull
    private ItemQuality quality;
    private int minAmount = 0;
    private int maxAmount = minAmount;
    private double chance = 0.0;

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

    public ItemQuality getQuality() {

        return quality;
    }

    public void setQuality(ItemQuality quality) {

        this.quality = quality;
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
