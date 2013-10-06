package de.raidcraft.loot.loottables;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.loot.LootPlugin;
import de.raidcraft.loot.api.table.AbstractLootTableQuality;
import de.raidcraft.loot.tables.TLootTableQuality;

/**
 * @author Silthus
 */
public class DatabaseLootTableQuality extends AbstractLootTableQuality {

    public DatabaseLootTableQuality(TLootTableQuality quality) {

        super(quality.getId(), quality.getQuality());
        setMinAmount(quality.getMinAmount());
        setMaxAmount(quality.getMaxAmount());
        setChance(quality.getChance());
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(LootPlugin.class);
        TLootTableQuality quality = database.find(TLootTableQuality.class, getId());
        quality.setMinAmount(getMinAmount());
        quality.setMaxAmount(getMaxAmount());
        quality.setChance(getChance());
        database.save(quality);
    }
}
