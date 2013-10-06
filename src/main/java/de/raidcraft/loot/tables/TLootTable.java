package de.raidcraft.loot.tables;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_tables")
public class TLootTable {

    @Id
    private int id;
    @OneToOne(mappedBy = "loot_table_id", cascade = CascadeType.REMOVE)
    private TLootTableAlias alias;
    private int minLoot;
    private int maxLoot;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "loot_table_id")
    private List<TLootTableEntry> lootTableEntries;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "loot_table_id")
    private List<TLootTableQuality> lootTableQualities;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TLootTableAlias getAlias() {

        return alias;
    }

    public void setAlias(TLootTableAlias alias) {

        this.alias = alias;
    }

    public int getMinLoot() {

        return minLoot;
    }

    public void setMinLoot(int minLoot) {

        this.minLoot = minLoot;
    }

    public int getMaxLoot() {

        return maxLoot;
    }

    public void setMaxLoot(int maxLoot) {

        this.maxLoot = maxLoot;
    }

    public List<TLootTableEntry> getLootTableEntries() {

        return lootTableEntries;
    }

    public void setLootTableEntries(List<TLootTableEntry> lootTableEntries) {

        this.lootTableEntries = lootTableEntries;
    }

    public List<TLootTableQuality> getLootTableQualities() {

        return lootTableQualities;
    }

    public void setLootTableQualities(List<TLootTableQuality> lootTableQualities) {

        this.lootTableQualities = lootTableQualities;
    }
}
