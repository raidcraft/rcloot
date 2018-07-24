package de.raidcraft.loot.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_entries")
@Data
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
}
