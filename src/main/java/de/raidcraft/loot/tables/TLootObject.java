package de.raidcraft.loot.tables;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * @author Dragonfire
 */
@Getter
@Setter
@Entity
@Table(name = "loot_objects")
public class TLootObject {

    @Id
    private int id;
    private int lootTableId;
    private String world;
    private int x;
    private int y;
    private int z;
    private int cooldown;
    private UUID creatorId;
    private Date creationDate;
    private boolean enabled;
    private int rewardLevel;
    private boolean publicChest;

    public void setLocation(Location loc) {

        setX((int) loc.getX());
        setY((int) loc.getY());
        setZ((int) loc.getZ());
    }

}