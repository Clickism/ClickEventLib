package me.clickism.clickeventlib.util;

import me.clickism.clickeventlib.serialization.AutoSaver;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Game player set uses {@link PlayerSet} to store players that are in-game and disqualified.
 *
 * <p>Players in-game are considered to be in-game and not disqualified.</p>
 * <p>Disqualified players are considered to be not in-game</p>
 */
public class GamePlayerSet {

    private final PlayerSet inGameSet;
    private final PlayerSet disqualifiedSet;

    /**
     * Creates a new game player set.
     *
     * @param plugin               plugin
     * @param autoSaver            auto saver
     * @param saveInterval         save interval
     * @param inGameFileName       file name for the in-game set
     * @param disqualifiedFileName file name for the disqualified set
     * @throws IOException if an error occurs while loading the player data files
     */
    public GamePlayerSet(JavaPlugin plugin, AutoSaver autoSaver, int saveInterval,
                         String inGameFileName, String disqualifiedFileName) throws IOException {
        this.inGameSet = new PlayerSet(plugin, autoSaver, saveInterval, inGameFileName);
        this.disqualifiedSet = new PlayerSet(plugin, autoSaver, saveInterval, disqualifiedFileName);
    }

    /**
     * Adds a player to the in-game set.
     *
     * @param player player to add
     */
    public void joinGame(Player player) {
        joinGame(player.getUniqueId());
    }

    /**
     * Adds a player to the in-game set.
     *
     * @param uuid uuid of the player to add
     */
    public void joinGame(UUID uuid) {
        disqualifiedSet.remove(uuid);
        inGameSet.add(uuid);
    }

    /**
     * Removes a player from the in-game set. This will also make the player not disqualified if they were.
     * The player will no longer be considered as "have played".
     *
     * @param player player to remove
     */
    public void leaveGame(Player player) {
        leaveGame(player.getUniqueId());
    }

    /**
     * Removes a player from the in-game set. This will also make the player not disqualified if they were.
     * The player will no longer be considered as "have played".
     *
     * @param uuid uuid of the player to remove
     */
    public void leaveGame(UUID uuid) {
        inGameSet.remove(uuid);
        disqualifiedSet.remove(uuid);
    }

    /**
     * Checks if a player is in-game and not disqualified.
     *
     * @param player player to check
     * @return true if the player is in the in-game set, false otherwise
     */
    public boolean isInGame(Player player) {
        return isInGame(player.getUniqueId());
    }

    /**
     * Checks if a player is in-game and not disqualified.
     *
     * @param uuid uuid of the player to check
     * @return true if the player is in the in-game set, false otherwise
     */
    public boolean isInGame(UUID uuid) {
        return inGameSet.contains(uuid);
    }

    /**
     * Checks if a player is disqualified.
     *
     * @param player player to check
     * @return true if the player is in the disqualified set, false otherwise
     */
    public boolean isDisqualified(Player player) {
        return isDisqualified(player.getUniqueId());
    }

    /**
     * Checks if a player is disqualified.
     *
     * @param uuid uuid of the player to check
     * @return true if the player is in the disqualified set, false otherwise
     */
    public boolean isDisqualified(UUID uuid) {
        return disqualifiedSet.contains(uuid);
    }

    /**
     * Disqualifies a player from the game.
     *
     * @param player player to disqualify
     */
    public void disqualify(Player player) {
        disqualify(player.getUniqueId());
    }

    /**
     * Disqualifies a player from the game.
     *
     * @param uuid uuid of the player to disqualify
     */
    public void disqualify(UUID uuid) {
        inGameSet.remove(uuid);
        disqualifiedSet.add(uuid);
    }

    /**
     * Checks if a player has played in the game.
     * A player is considered to have played if they are either still in-game, or were disqualified.
     *
     * @param player player to check
     * @return true if the player has played, false otherwise
     */
    public boolean hasPlayed(Player player) {
        return hasPlayed(player.getUniqueId());
    }

    /**
     * Checks if a player has played in the game.
     * A player is considered to have played if they are either still in-game, or were disqualified.
     *
     * @param uuid uuid of the player to check
     * @return true if the player has played, false otherwise
     */
    public boolean hasPlayed(UUID uuid) {
        return inGameSet.contains(uuid) || disqualifiedSet.contains(uuid);
    }

    /**
     * Clears the in-game and disqualified sets.
     */
    public void clear() {
        inGameSet.clear();
        disqualifiedSet.clear();
    }

    /**
     * Gets the set of players that are in-game.
     *
     * @return the set of players that are in-game
     */
    public PlayerSet getInGameSet() {
        return inGameSet;
    }

    /**
     * Gets the set of players that are disqualified.
     *
     * @return the set of players that are disqualified
     */
    public PlayerSet getDisqualifiedSet() {
        return disqualifiedSet;
    }

    /**
     * Gets the players that are in-game.
     *
     * @return the players in-game
     */
    public List<Player> getInGamePlayers() {
        return inGameSet.getPlayers();
    }

    /**
     * Gets the players that are disqualified.
     *
     * @return the players disqualified
     */
    public List<Player> getDisqualifiedPlayers() {
        return disqualifiedSet.getPlayers();
    }

    /**
     * Gets the UUIDs of the players that are in-game.
     *
     * @return the UUIDs of the players in-game
     */
    public Collection<UUID> getInGameUUIDs() {
        return inGameSet.getUUIDs();
    }

    /**
     * Gets the UUIDs of the players that were disqualified.
     *
     * @return the UUIDs of the players that were disqualified
     */
    public Collection<UUID> getDisqualifiedUUIDs() {
        return disqualifiedSet.getUUIDs();
    }
}
