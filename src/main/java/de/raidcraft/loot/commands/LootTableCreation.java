package de.raidcraft.loot.commands;

/**
 * @author Silthus
 */
public class LootTableCreation {

    private final String alias;
    private final int minAmount;
    private final int maxAmount;

    public LootTableCreation(String alias, int minAmount, int maxAmount) {

        this.alias = alias;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public String getAlias() {

        return alias;
    }

    public int getMinAmount() {

        return minAmount;
    }

    public int getMaxAmount() {

        return maxAmount;
    }
}
