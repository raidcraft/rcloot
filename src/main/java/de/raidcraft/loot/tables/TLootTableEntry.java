package de.raidcraft.loot.tables;

import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rc_loot_table_entries")
@Data
@EqualsAndHashCode(of = "id")
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
