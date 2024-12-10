package me.clickism.clickeventlib.statistic;

import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.serialization.AutoSaved;
import me.clickism.clickeventlib.serialization.AutoSaver;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.clickeventlib.util.NamedCollection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Statistic manager.
 */
public class StatisticManager implements AutoSaved {
    private static final int OPERATIONS_BEFORE_SAVE = 10;

    private final JSONDataManager dataManager;

    private final NamedCollection<Statistic<?>> statistics = new NamedCollection<>(new ArrayList<>());

    /**
     * Create a new statistic manager.
     *
     * @param plugin       plugin
     * @param autoSaver    auto saver to register save on disable with
     * @param saveInterval save interval in seconds
     * @param fileName     file name
     * @throws IOException if an I/O error occurs
     */
    @AutoRegistered(type = RegistryType.SAVE_ON_DISABLE)
    public StatisticManager(JavaPlugin plugin, AutoSaver autoSaver, int saveInterval, String fileName) throws IOException {
        autoSaver.registerSaveOnDisable(this);
        autoSaver.registerSaveOnInterval(this, saveInterval);
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
    }

    /**
     * Register a statistic.
     *
     * @param statistic statistic to register
     * @param <T>       type of the statistic's value
     * @return the registered statistic
     */
    public <T extends Comparable<T>> Statistic<T> register(Statistic<T> statistic) {
        statistics.add(statistic);
        load(statistic);
        return statistic;
    }
    
    /**
     * Get the collection of all statistics.
     *
     * @return the collection of all statistics
     */
    public NamedCollection<Statistic<?>> getStatistics() {
        return statistics;
    }

    @Override
    public void save() {
        JsonObject json = new JsonObject();
        for (Statistic<?> statistic : statistics) {
            json.add(statistic.getName(), toJson(statistic));
        }
        dataManager.save(json);
    }

    private JsonObject toJson(Statistic<?> statistic) {
        JsonObject json = new JsonObject();
        json.add("players", JSONDataManager.GSON.toJsonTree(statistic.getMap(), Map.class));
        return json;
    }

    private <T extends Comparable<T>> void load(Statistic<T> statistic) {
        JsonObject root = dataManager.getRoot();
        String id = statistic.getName();
        if (!root.has(id)) return;
        JsonObject playersNode = root.getAsJsonObject(id).getAsJsonObject("players");
        playersNode.keySet().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            String value = playersNode.get(key).getAsString();
            statistic.parseAndSet(uuid, value);
        });
    }
}
