package de.raidcraft.loot.tables;

import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.WhenModified;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "rcloot_players")
@Data
public class TLootPlayer {

    @Id
    private int id;
    @NotNull
    private UUID playerId;
    @NotNull
    @OneToMany
    private TLootObject lootObject;
    @CreatedTimestamp
    private Timestamp created;
    @WhenModified
    private Timestamp updated;
}
