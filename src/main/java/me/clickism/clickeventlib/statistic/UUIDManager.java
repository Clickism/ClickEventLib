package me.clickism.clickeventlib.statistic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.serialization.AutoSaved;
import me.clickism.clickeventlib.serialization.AutoSaver;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Saves and manages UUIDs and names of players.
 */
public class UUIDManager implements AutoSaved, Listener {
    /**
     * The singleton instance of the offline player manager.
     */
    public static UUIDManager INSTANCE;

    /**
     * Creates a new instance of the UUID manager.
     *
     * @param plugin    the plugin to create the instance for
     * @param autoSaver the auto saver to use
     * @throws IOException           if an error occurs while loading the data
     * @throws IllegalStateException if the instance is already initialized
     */
    public static void createInstance(JavaPlugin plugin, AutoSaver autoSaver) throws IOException, IllegalArgumentException {
        if (INSTANCE != null) {
            throw new IllegalStateException("UUIDManager is already initialized");
        }
        INSTANCE = new UUIDManager(plugin, autoSaver);
    }

    private final JSONDataManager dataManager;

    private final Map<UUID, String> uuidToName = new HashMap<>();
    private final Map<String, UUID> nameToUUID = new HashMap<>();

    @AutoRegistered(type = {RegistryType.EVENT, RegistryType.SAVE_ON_DISABLE})
    private UUIDManager(JavaPlugin plugin, AutoSaver autoSaver) throws IOException {
        dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), "uuids.json");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        autoSaver.registerSaveOnDisable(this);
        load();
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer());
    }

    private void updatePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        uuidToName.put(uuid, name);
        nameToUUID.put(name, uuid);
    }

    /**
     * Gets the name of a player by their UUID.
     *
     * @param uuid the UUID of the player
     * @return the name of the player, or null if the player is not known
     */
    @Nullable
    public static String getName(UUID uuid) {
        if (INSTANCE == null) return null;
        return INSTANCE.uuidToName.get(uuid);
    }

    /**
     * Gets the UUID of a player by their name.
     *
     * @param name the name of the player
     * @return the UUID of the player, or null if the player is not known
     */
    @Nullable
    public static UUID getUUID(String name) {
        if (INSTANCE == null) return null;
        return INSTANCE.nameToUUID.get(name);
    }

    @Override
    public void save() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        uuidToName.forEach((uuid, name) -> {
            JsonObject object = new JsonObject();
            object.addProperty("uuid", uuid.toString());
            object.addProperty("name", name);
            array.add(object);
        });
        json.add("players", array);
        dataManager.save(json);
    }

    private void load() {
        JsonObject root = dataManager.getRoot();
        if (!root.has("players")) return;
        JsonArray array = root.getAsJsonArray("players");
        array.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            UUID uuid = UUID.fromString(object.get("uuid").getAsString());
            String name = object.get("name").getAsString();
            uuidToName.put(uuid, name);
            nameToUUID.put(name, uuid);
        });
    }
}
