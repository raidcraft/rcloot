package de.raidcraft.loot.tables;

import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "rcloot_players")
@Data
@EqualsAndHashCode(of = "id")
public class TLootPlayer {

    @Id
    private int id;
    @NotNull
    private UUID playerId;
    @NotNull
    @ManyToOne
    private TLootObject lootObject;
    @CreatedTimestamp
    private Timestamp created = new Timestamp(System.currentTimeMillis());
}
