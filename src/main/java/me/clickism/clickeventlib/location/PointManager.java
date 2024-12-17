package me.clickism.clickeventlib.location;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.item.CustomItem;
import me.clickism.clickeventlib.item.CustomItemManager;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Used for dynamic (unnamed) points that can be easily added and removed. Such as spawnpoints, chest locations etc.
 */
public class PointManager {
    private final NamedCollection<EventLocation> points = new NamedCollection<>(new ArrayList<>());
    private final JSONDataManager dataManager;
    private final String pointNamespace;

    private final CustomItem adderItem;
    private final CustomItem selectorItem;

    private int nextPointId = 0;

    /**
     * Create a new point manager.
     *
     * @param plugin          the plugin
     * @param itemManager     the item manager to register the adder and selector items with
     * @param pluginNamespace the namespace of the plugin
     * @param pointNamespace  the namespace of the points (i.E.: "pvp_spawnpoint").
     *                        This will also be used for the commands.
     * @param fileName        the file name to save the points to
     * @throws IOException if an error occurs while loading the points
     */
    public PointManager(JavaPlugin plugin, CustomItemManager itemManager, String pluginNamespace, String pointNamespace,
                        String fileName) throws IOException {
        this.pointNamespace = pointNamespace;
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
        this.adderItem = itemManager.register(new PointAdderItem(pluginNamespace, this));
        this.selectorItem = itemManager.register(new PointSelectorItem(pluginNamespace, this));
        load();
    }

    /**
     * Get the adder item.
     *
     * @return the adder item
     */
    public CustomItem getAdderItem() {
        return adderItem;
    }

    /**
     * Get the selector item.
     *
     * @return the selector item
     */
    public CustomItem getSelectorItem() {
        return selectorItem;
    }

    /**
     * Get the namespace of the points.
     *
     * @return the namespace of the points
     */
    public String getPointNamespace() {
        return pointNamespace;
    }

    /**
     * Get all points.
     *
     * @return all points
     */
    public NamedCollection<EventLocation> getPoints() {
        return points;
    }

    /**
     * Get all point locations that are set.
     *
     * @return all point locations that are set
     */
    public List<Location> getPointLocations() {
        return points.stream()
                .map(EventLocation::getLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get a random point location. Returns null if there are no set points.
     *
     * @return random point location or null if there are no set points.
     */
    @Nullable
    public Location getRandomPointLocation() {
        List<Location> locations = points.stream()
                .map(EventLocation::getLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
        if (locations.isEmpty()) return null;
        return locations.get((int) (Math.random() * locations.size()));
    }

    /**
     * Add a point.
     *
     * @param location the location of the point
     * @return the added point
     */
    public EventLocation addPoint(Location location) {
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Spawnpoint world is null.");
        }
        String worldName = world.getName();
        EventLocation eventLocation = nextEventLocation();
        eventLocation.setLocation(worldName, location);
        points.add(eventLocation);
        save();
        return eventLocation;
    }

    /**
     * Remove a point.
     *
     * @param location the point to remove
     */
    public void removePoint(EventLocation location) {
        points.remove(location);
        save();
    }

    private EventLocation nextEventLocation() {
        return new EventLocation(pointNamespace + "_" + nextPointId++);
    }

    private void save() {
        JsonObject json = new JsonObject();
        JsonArray pointsArray = new JsonArray();
        for (EventLocation point : points) {
            SafeLocation safeLocation = point.getSafeLocation();
            JsonObject pointObject = JSONDataManager.GSON.toJsonTree(safeLocation, SafeLocation.class).getAsJsonObject();
            pointsArray.add(pointObject);
        }
        json.add("points", pointsArray);
        dataManager.save(json);
    }

    private void load() {
        JsonObject root = dataManager.getRoot();
        if (!root.has("points")) return;
        JsonArray pointsArray = root.getAsJsonArray("points");
        for (JsonElement point : pointsArray) {
            SafeLocation safeLocation = JSONDataManager.GSON.fromJson(point, SafeLocation.class);
            EventLocation eventLocation = nextEventLocation();
            eventLocation.setSafeLocation(safeLocation);
            points.add(eventLocation);
        }
    }
}
