package me.clickism.clickeventlib.location;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages worlds.
 */
public class WorldManager implements Listener {
    private final JSONDataManager dataManager;
    private final Map<String, World> worldMap = new HashMap<>();
    private final List<String> alwaysLoadedWorlds = new ArrayList<>();

    private final JavaPlugin plugin;

    /**
     * Creates a new world manager.
     *
     * @param plugin   plugin
     * @param fileName file name to store the world data
     * @throws IOException if the data couldn't be loaded
     */
    public WorldManager(JavaPlugin plugin, String fileName) throws IOException {
        this.plugin = plugin;
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        try {
            load();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to import world(s): " + e.getMessage());
        }
    }

    /**
     * Get all world names
     *
     * @return collection of world names
     */
    public Set<String> getWorldNames() {
        return worldMap.keySet();
    }

    /**
     * Get a world by key.
     *
     * @param name key of the world
     * @return world or null if it doesn't exist
     */
    @Nullable
    public World getWorld(String name) {
        World world = worldMap.get(name);
        if (world != null) {
            return world;
        }
        try {
            importWorld(name);
            return worldMap.get(name);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Generate a new world with the given world creator.
     *
     * @param worldCreator world creator to use
     * @throws IllegalArgumentException if the world couldn't be generated
     * @throws IOException              if the world couldn't be generated
     */
    public void generateWorld(WorldCreator worldCreator) throws IllegalArgumentException, IOException {
        World world = worldCreator.createWorld();
        String worldName = worldCreator.name();
        if (world == null) {
            throw new IllegalArgumentException("Couldn't generate/import world " + worldName + ".");
        }
        plugin.getLogger().info("Generated/imported world " + worldName + ".");
        worldMap.put(worldName, world);
        save();
    }

    /**
     * Imports a world if it exists.
     *
     * @param name key of the world
     * @throws IllegalArgumentException if the world doesn't exist
     * @throws IOException              if the world couldn't be imported
     */
    public void importWorld(String name) throws IllegalArgumentException, IOException {
        if (worldMap.get(name) != null) return;
        if (!worldExists(name)) {
            throw new IllegalArgumentException("World " + name + " doesn't exist.");
        }
        plugin.getLogger().info("Importing world " + name + "...");
        generateWorld(new WorldCreator(name));
    }

    private boolean worldExists(String name) {
        return new File(Bukkit.getWorldContainer(), name).isDirectory();
    }

    /**
     * Registers and loads a world with the given name. This world will be automatically loaded on server restarts.
     *
     * @param name name of the world
     * @return if the world was successfully loaded
     */
    public boolean registerAlwaysLoadedWorld(String name) {
        alwaysLoadedWorlds.add(name);
        if (worldExists(name)) {
            try {
                importWorld(name);
            } catch (IOException e) {
                return false;
            }
        }
        save();
        return true;
    }

    private void load() throws IOException {
        JsonObject root = dataManager.getRoot();
        if (!root.has("worlds")) return;
        for (JsonElement worldNode : root.getAsJsonArray("worlds")) {
            String worldName = worldNode.getAsString();
            World world = Bukkit.getWorld(worldName);
            worldMap.put(worldName, world);
        }
        if (!root.has("always_loaded_worlds")) return;
        alwaysLoadedWorlds.clear();
        for (JsonElement worldNode : root.getAsJsonArray("always_loaded_worlds")) {
            String worldName = worldNode.getAsString();
            if (worldExists(worldName)) {
                importWorld(worldName);
            }
            alwaysLoadedWorlds.add(worldName);
        }
    }

    private void save() {
        JsonObject json = new JsonObject();
        JsonArray worlds = new JsonArray();
        for (String worldName : worldMap.keySet()) {
            worlds.add(worldName);
        }
        json.add("worlds", worlds);
        JsonArray alwaysLoaded = new JsonArray();
        for (String worldName : alwaysLoadedWorlds) {
            alwaysLoaded.add(worldName);
        }
        json.add("always_loaded_worlds", alwaysLoaded);
        dataManager.save(json);
    }
}
