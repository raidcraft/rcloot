package de.raidcraft.loot.tables;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

/**
 * @author Silthus
 */

@Entity
@Table(name = "rcloot_tables")
@Data
@EqualsAndHashCode(of = "id")
public class TLootTable {

    @Id
    private int id;
    @OneToOne(mappedBy = "lootTable", cascade = CascadeType.REMOVE)
    private TLootTableAlias lootTableAlias;
    private int minLoot;
    private int maxLoot;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "loot_table_id")
    private List<TLootTableEntry> lootTableEntries;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "loot_table_id")
    private List<TLootTableQuality> lootTableQualities;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "loot_table_id")
    private List<TLootObject> lootObjects;
}
