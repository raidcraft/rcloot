package de.raidcraft.loot.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ebean.BaseModel;
import de.raidcraft.loot.LootPlugin;
import io.ebean.EbeanServer;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.block.Block;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "rc_loot_objects")
@Data
@EqualsAndHashCode(callSuper = true)
public class TLootObject extends BaseModel {

    public static final LootObjectFinder find = new LootObjectFinder();

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
    private boolean publicLootObject = false;
    @DbDefault("false")
    private boolean destroyable = false;
    private Instant destroyed = null;
    private String material = null;
    @Column(length = 2048)
    private String blockData = null;
    @Column(length = 2048)
    private String extraData = null;

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
        setMaterial(block.getType().name());
        setBlockData(block.getBlockData().getAsString());
    }

    @Override
    protected EbeanServer database() {
        return RaidCraft.getDatabase(LootPlugin.class);
    }
}
