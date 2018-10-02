package de.raidcraft.loot.tables;

import io.ebean.annotation.DbDefault;
import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.block.Block;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "rc_loot_objects")
@Data
@EqualsAndHashCode(of = "id")
public class TLootObject {

    @Id
    private int id;
    @NotNull
    @DbDefault("")
    private String lootTable;
    @NotNull
    private String world;
    @NotNull
    private int x;
    @NotNull
    private int y;
    @NotNull
    private int z;
    private boolean enabled = true;
    private int cooldown = 0;
    private boolean infinite = false;
    /**
     * Public means all players share the same cooldown and loot.
     */
    private boolean publicLootObject = false;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "loot_object_id")
    private List<TLootPlayer> lootPlayers;

    public TLootObject() {
    }

    public TLootObject(Block block) {
        setWorld(block.getWorld().getName());
        setX(block.getX());
        setY(block.getY());
        setZ(block.getZ());
    }
}
