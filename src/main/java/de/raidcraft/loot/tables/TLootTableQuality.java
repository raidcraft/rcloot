package de.raidcraft.loot.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.api.items.ItemQuality;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_qualities")
@Data
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
}
