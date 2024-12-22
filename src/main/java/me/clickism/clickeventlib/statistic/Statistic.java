package me.clickism.clickeventlib.statistic;

import me.clickism.clickeventlib.leaderboard.LeaderboardEntryProvider;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Statistic class.
 *
 * @param <T> type of the statistic
 */
public class Statistic<T> implements Named, LeaderboardEntryProvider {
    private final StatisticType<T> type;
    private final String name;
    private final Map<UUID, T> map = new HashMap<>();

    private final T defaultValue;

    @Nullable
    private final Comparator<T> comparator;

    /**
     * Creates a new statistic with the given type and identifier.
     * Without a comparator, the leaderboard order is undefined.
     *
     * @param type         type of the statistic
     * @param name         identifier of the statistic
     * @param defaultValue default value of the statistic
     */
    public Statistic(StatisticType<T> type, String name, T defaultValue) {
        this(type, name, defaultValue, null);
    }

    /**
     * Creates a new statistic with the given type, identifier, default value, and comparator.
     * The comparator is used for the leaderboard.
     *
     * @param type         type of the statistic
     * @param name         identifier of the statistic
     * @param defaultValue default value of the statistic
     * @param comparator   comparator for the leaderboard
     */
    public Statistic(StatisticType<T> type, String name, T defaultValue, @Nullable Comparator<T> comparator) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.comparator = comparator;
    }

    /**
     * Set the value of the statistic for the given player.
     *
     * @param player the player
     * @param value  the new value of the statistic
     */
    public void set(Player player, T value) {
        set(player.getUniqueId(), value);
    }

    /**
     * Set the value of the statistic for the given uuid.
     *
     * @param uuid  the UUID
     * @param value the new value of the statistic
     */
    public void set(UUID uuid, T value) {
        map.put(uuid, value);
    }

    /**
     * Increment the value of the statistic by the given increment for the given player.
     *
     * @param player    the player
     * @param increment the increment
     */
    public void incrementBy(Player player, T increment) {
        incrementBy(player.getUniqueId(), increment);
    }

    /**
     * Increment the value of the statistic by the given increment for the given UUID.
     *
     * @param uuid      the UUID of the player
     * @param increment the increment
     */
    public void incrementBy(UUID uuid, T increment) {
        T value = getOrDefault(uuid);
        map.put(uuid, type.sum(value, increment));
    }

    /**
     * Parse the given string for the statistic type and set the value for the given player.
     *
     * @param uuid   the UUID
     * @param string the string to parse
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public void parseAndSet(UUID uuid, String string) throws IllegalArgumentException {
        T value = parseOrNull(string);
        if (value == null) throw new IllegalArgumentException("Invalid value: " + string);
        set(uuid, value);
    }

    /**
     * Parse the given string for the statistic type and increment the value for the given player.
     *
     * @param uuid   the UUID
     * @param string the string to parse
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public void parseAndIncrementBy(UUID uuid, String string) throws IllegalArgumentException {
        T value = parseOrNull(string);
        if (value == null) throw new IllegalArgumentException("Invalid value: " + string);
        incrementBy(uuid, value);
    }

    /**
     * Parse the given string for the statistic type.
     * Return null if the string cannot be parsed.
     *
     * @param value the string to parse
     * @return the parsed value or null
     */
    @Nullable
    private T parseOrNull(String value) {
        try {
            return type.parse(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Remove the value of the statistic for the given player.
     *
     * @param player the player
     */
    public void remove(Player player) {
        remove(player.getUniqueId());
    }

    /**
     * Remove the value of the statistic for the given UUID.
     *
     * @param uuid the UUID
     */
    public void remove(UUID uuid) {
        map.remove(uuid);
    }

    /**
     * Clear all values of the statistic.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Get the value of the statistic for the given player.
     *
     * @param player the player
     * @return the value of the statistic
     */
    @Nullable
    public T getOrNull(Player player) {
        return getOrNull(player.getUniqueId());
    }

    /**
     * Get the value of the statistic for the given UUID.
     *
     * @param uuid the UUID
     * @return the value of the statistic
     */
    @Nullable
    public T getOrNull(UUID uuid) {
        return map.get(uuid);
    }

    /**
     * Get the value of the statistic for the given player or the default value if the player does not have a value.
     *
     * @param player the player
     * @return the value of the statistic
     */
    @NotNull
    public T getOrDefault(Player player) {
        return getOrDefault(player.getUniqueId());
    }

    /**
     * Get the value of the statistic for the given UUID or the default value if the UUID does not have a value.
     *
     * @param uuid the UUID
     * @return the value of the statistic
     */
    @NotNull
    public T getOrDefault(UUID uuid) {
        T value = getOrNull(uuid);
        return value != null ? value : defaultValue;
    }

    /**
     * Get the map of UUIDs to values.
     *
     * @return the map of UUIDs to values
     */
    public Map<UUID, T> getMap() {
        return map;
    }

    /**
     * Get the type of the statistic.
     *
     * @return the type of the statistic
     */
    public StatisticType<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public @NotNull List<Map.Entry<UUID, String>> getLeaderboardEntries() {
        Stream<Map.Entry<UUID, T>> stream = map.entrySet().stream()
                .filter(entry -> entry.getValue() != null);
        if (comparator != null) {
            stream = stream.sorted(Map.Entry.comparingByValue(comparator));
        }
        return stream.map(entry -> Map.entry(entry.getKey(), type.format(entry.getValue())))
                .toList();
    }
}
