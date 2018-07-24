package de.raidcraft.loot.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Data;

import javax.persistence.*;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_aliases")
@Data
public class TLootTableAlias {

    @Id
    private int id;
    @OneToOne
    private TLootTable lootTable;
    @NotNull
    @Column(unique = true)
    private String tableAlias;
}