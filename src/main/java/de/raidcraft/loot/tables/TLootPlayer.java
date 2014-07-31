package de.raidcraft.loot.tables;

import lombok.Getter;
import lombok.Setter;

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
@Table(name = "loot_players")
public class TLootPlayer {

    @Id
    private int id;
    private int objectId;
    private UUID playerId;
    private Date openTime;

}
