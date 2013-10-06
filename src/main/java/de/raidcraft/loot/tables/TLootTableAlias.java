package de.raidcraft.loot.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcloot_table_aliases")
public class TLootTableAlias {

    @Id
    private int id;
    @OneToOne
    private TLootTable lootTable;
    @NotNull
    @Column(unique = true)
    private String alias;

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

    public String getAlias() {

        return alias;
    }

    public void setAlias(String alias) {

        this.alias = alias;
    }
}