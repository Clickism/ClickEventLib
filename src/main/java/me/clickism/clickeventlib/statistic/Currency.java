package me.clickism.clickeventlib.statistic;

import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.UUID;

/**
 * A statistic that represents a currency.
 * <p>
 * This class provides functionality to send a message to the player when the currency is incremented.
 */
public abstract class Currency extends Statistic<Integer> {
    /**
     * Creates a new currency with the given id and default value.
     *
     * @param name         name of the currency
     * @param defaultValue the default value
     */
    public Currency(String name, int defaultValue) {
        super(StatisticType.INTEGER, name, defaultValue, Comparator.reverseOrder());
    }

    /**
     * Increments the currency by the given amount and sends a message to the player
     * if the increment is greater than 0.
     *
     * @param player    the player
     * @param increment the increment
     */
    public void incrementByAndNotify(Player player, Integer increment) {
        super.incrementBy(player, increment);
        if (increment <= 0) return;
        sendMessage(player, increment);
    }

    /**
     * Increments the currency by the given amount and sends a message to the player.
     *
     * @param player player
     * @param amount the amount to increment
     * @return true if the player had enough currency and it was decremented, false otherwise
     */
    public boolean decrementIfHasEnough(Player player, Integer amount) {
        return decrementIfHasEnough(player.getUniqueId(), amount);
    }

    /**
     * Decrements the currency by the given amount if the player has enough currency.
     *
     * @param uuid   player's uuid
     * @param amount the amount to decrement
     * @return true if the player had enough currency and it was decremented, false otherwise
     */
    public boolean decrementIfHasEnough(UUID uuid, Integer amount) {
        if (hasAtLeast(uuid, amount)) {
            incrementBy(uuid, -amount);
            return true;
        }
        return false;
    }

    /**
     * Checks if the player has at least the given amount of currency.
     *
     * @param player player
     * @param amount the amount to check
     * @return true if the player has at least the given amount of currency, false otherwise
     */
    public boolean hasAtLeast(Player player, Integer amount) {
        return hasAtLeast(player.getUniqueId(), amount);
    }

    /**
     * Checks if the player has at least the given amount of currency.
     *
     * @param uuid   player's uuid
     * @param amount the amount to check
     * @return true if the player has at least the given amount of currency, false otherwise
     */
    public boolean hasAtLeast(UUID uuid, Integer amount) {
        return getOrDefault(uuid) >= amount;
    }

    /**
     * This method is called when the currency is incremented.
     *
     * @param player    player to send the message to
     * @param increment the increment
     */
    public abstract void sendMessage(Player player, Integer increment);
}
