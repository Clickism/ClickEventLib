package me.clickism.clickeventlib.location;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Manager for event locations.
 */
public class EventLocationManager {
    /**
     * Data manager for saving and loading locations.
     */
    protected final JSONDataManager dataManager;
    /**
     * Collection of event locations.
     */
    protected final NamedCollection<EventLocation> eventLocations = new NamedCollection<>(new ArrayList<>());

    /**
     * Create a new event location manager.
     *
     * @param plugin   plugin to register locations with
     * @param fileName name of the file to save locations to
     * @throws IOException if an error occurs while loading the locations file
     */
    public EventLocationManager(JavaPlugin plugin, String fileName) throws IOException {
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
    }

    /**
     * Register an event location.
     *
     * @param eventLocation event location to register
     */
    public void register(EventLocation eventLocation) {
        eventLocations.add(eventLocation);
        tryLoad(eventLocation);
    }

    /**
     * Try to load the location from the data manager.
     *
     * @param eventLocation event location
     */
    protected void tryLoad(EventLocation eventLocation) {
        JsonObject root = dataManager.getRoot();
        if (root.has(eventLocation.getName())) {
            JsonElement locationNode = root.get(eventLocation.getName());
            SafeLocation safeLocation = JSONDataManager.GSON.fromJson(locationNode, SafeLocation.class);
            eventLocation.setSafeLocation(safeLocation);
        }
    }

    /**
     * Save all event locations.
     */
    public void save() {
        JsonObject root = new JsonObject();
        for (EventLocation eventLocation : eventLocations) {
            root.add(eventLocation.getName(), JSONDataManager.GSON.toJsonTree(eventLocation.getSafeLocation()));
        }
        dataManager.save(root);
    }

    /**
     * Get the collection of event locations.
     *
     * @return collection of event locations
     */
    public NamedCollection<EventLocation> getEventLocations() {
        return eventLocations;
    }
}
