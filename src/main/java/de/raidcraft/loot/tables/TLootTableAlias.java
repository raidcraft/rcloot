package de.raidcraft.loot.tables;

import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_aliases")
@Data
@EqualsAndHashCode(of = "id")
public class TLootTableAlias {

    @Id
    private int id;
    @OneToOne
    private TLootTable lootTable;
    @NotNull
    @Column(unique = true)
    private String tableAlias;
}