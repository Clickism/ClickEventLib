package me.clickism.clickeventlib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.serialization.AutoSaved;
import me.clickism.clickeventlib.serialization.AutoSaver;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Used for safely storing a set of players.
 * Players are stored by their UUID, and are saved to a JSON file on interval and on server disable.
 */
public class PlayerSet implements AutoSaved {

    private final Set<UUID> players = new HashSet<>();

    private final JSONDataManager dataManager;

    /**
     * Creates a new player set.
     *
     * @param plugin       plugin
     * @param autoSaver    auto saver
     * @param saveInterval save interval
     * @param fileName     file name
     * @throws IOException if an error occurs while loading the player data file
     */
    @AutoRegistered(type = {RegistryType.SAVE_ON_INTERVAL, RegistryType.SAVE_ON_DISABLE})
    public PlayerSet(JavaPlugin plugin, AutoSaver autoSaver, int saveInterval, String fileName) throws IOException {
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
        autoSaver.registerSaveOnInterval(this, saveInterval);
        autoSaver.registerSaveOnDisable(this);
    }

    /**
     * Adds a player to the player set.
     *
     * @param player player to add
     */
    public void add(Player player) {
        add(player.getUniqueId());
    }

    /**
     * Adds a player to the player set.
     *
     * @param uuid uuid of the player to add
     */
    public void add(UUID uuid) {
        players.add(uuid);
    }

    /**
     * Removes a player from the player set.
     *
     * @param player player to remove
     * @return true if the player was removed, false otherwise
     */
    public boolean remove(Player player) {
        return remove(player.getUniqueId());
    }

    /**
     * Removes a player from the player set.
     *
     * @param uuid uuid of the player to remove
     * @return true if the player was removed, false otherwise
     */
    public boolean remove(UUID uuid) {
        return players.remove(uuid);
    }

    /**
     * Checks if the player set contains a player.
     *
     * @param player player to check
     * @return true if the player is in the player set, false otherwise
     */
    public boolean contains(Player player) {
        return contains(player.getUniqueId());
    }

    /**
     * Checks if the player set contains a player.
     *
     * @param uuid uuid of the player to check
     * @return true if the player is in the player set, false otherwise
     */
    public boolean contains(UUID uuid) {
        return players.contains(uuid);
    }

    /**
     * Gets all players that are online and in the player set.
     *
     * @return list of players
     */
    public List<Player> getPlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> players.contains(player.getUniqueId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets uuids of all the players in the player set.
     *
     * @return collection of uuids
     */
    public Collection<UUID> getUUIDs() {
        return players;
    }

    /**
     * Clears all players from the player set.
     */
    public void clear() {
        players.clear();
    }

    @Override
    public void save() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        players.forEach(uuid -> array.add(uuid.toString()));
        json.add("players", array);
        dataManager.save(json);
    }

    private void load() {
        JsonObject root = dataManager.getRoot();
        if (!root.has("players")) return;
        JsonArray array = root.getAsJsonArray("players");
        array.forEach(element -> {
            String uuid = element.getAsString();
            players.add(UUID.fromString(uuid));
        });
    }
}
