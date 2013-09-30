package de.raidcraft.loot.database.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "loot_aliases")
public class TLootTableAlias {

    @Id
    private int id;
    private String alias;
    private int lootTableId;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getAlias() {

        return alias;
    }

    public void setAlias(String alias) {

        this.alias = alias;
    }

    public int getLootTableId() {

        return lootTableId;
    }

    public void setLootTableId(int lootTableId) {

        this.lootTableId = lootTableId;
    }
}
