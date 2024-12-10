package me.clickism.clickeventlib.leaderboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.location.SafeLocation;
import me.clickism.clickeventlib.serialization.AutoSaved;
import me.clickism.clickeventlib.serialization.AutoSaver;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.clickeventlib.util.NamedCollection;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Manager for leaderboards.
 */
public class LeaderboardManager implements AutoSaved {

    private final JSONDataManager dataManager;

    private final NamedCollection<LeaderboardEntryProvider> providers = new NamedCollection<>(new ArrayList<>());
    private final HashMap<LeaderboardEntryProvider, Integer> nextIdMap = new HashMap<>();

    private final NamedCollection<Leaderboard> leaderboards = new NamedCollection<>(new ArrayList<>());

    /**
     * Create a new leaderboard manager.
     *
     * @param plugin       plugin
     * @param autoSaver    auto saver
     * @param saveInterval save interval
     * @param fileName     file name
     * @throws IOException if an error occurs while loading the leaderboards file
     */
    @AutoRegistered(type = {RegistryType.SAVE_ON_INTERVAL, RegistryType.SAVE_ON_DISABLE})
    public LeaderboardManager(JavaPlugin plugin, AutoSaver autoSaver, int saveInterval, String fileName) throws IOException {
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
        autoSaver.registerSaveOnInterval(this, saveInterval);
        autoSaver.registerSaveOnDisable(this);
    }

    /**
     * Register a leaderboard entry provider.
     *
     * @param provider provider
     */
    public void registerProvider(LeaderboardEntryProvider provider) {
        providers.add(provider);
        tryLoad(provider);
    }

    /**
     * Add a leaderboard.
     *
     * @param leaderboard leaderboard
     */
    public void addLeaderboard(Leaderboard leaderboard) {
        leaderboards.add(leaderboard);
        LeaderboardEntryProvider provider = leaderboard.getProvider();
        nextIdMap.put(provider, leaderboard.getId() + 1);
    }

    /**
     * Remove a leaderboard.
     *
     * @param leaderboard leaderboard
     */
    public void removeLeaderboard(Leaderboard leaderboard) {
        leaderboards.remove(leaderboard);
        leaderboard.remove();
    }

    /**
     * Update all leaderboards.
     */
    public void updateLeaderboards() {
        leaderboards.forEach(Leaderboard::update);
    }

    /**
     * Get the next ID for a provider.
     *
     * @param provider provider
     * @return next ID
     */
    public int getNextId(LeaderboardEntryProvider provider) {
        return nextIdMap.getOrDefault(provider, 0);
    }

    /**
     * Get a collection of all registered leaderboard entry providers.
     *
     * @return leaderboard entry providers
     */
    public NamedCollection<LeaderboardEntryProvider> getProviders() {
        return providers;
    }

    /**
     * Get a collection of all registered leaderboards.
     *
     * @return leaderboards
     */
    public NamedCollection<Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    @Override
    public void save() {
        updateLeaderboards();
        JsonObject json = new JsonObject();
        providers.forEach(provider -> json.add(provider.getName(), new JsonArray()));
        leaderboards.forEach(leaderboard -> {
            String name = leaderboard.getProvider().getName();
            JsonArray array = json.getAsJsonArray(name);
            array.add(toJson(leaderboard));
        });
        dataManager.save(json);
    }

    private JsonObject toJson(Leaderboard leaderboard) {
        JsonObject json = new JsonObject();
        json.add("location", JSONDataManager.GSON.toJsonTree(leaderboard.getSafeLocation()));
        json.addProperty("title", leaderboard.getTitle());
        json.addProperty("color", leaderboard.getColor().name());
        json.addProperty("entryCount", leaderboard.getEntryCount());
        json.addProperty("scale", leaderboard.getScale());
        json.addProperty("textUUID", leaderboard.getTextUUID().toString());
        return json;
    }

    private void tryLoad(LeaderboardEntryProvider provider) {
        JsonObject root = dataManager.getRoot();
        String providerName = provider.getName();
        if (!root.has(providerName)) return;
        JsonArray array = root.getAsJsonArray(providerName);
        for (JsonElement jsonElement : array) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Leaderboard leaderboard = fromJson(jsonObject, provider);
            addLeaderboard(leaderboard);
        }
    }

    private Leaderboard fromJson(JsonObject json, LeaderboardEntryProvider provider) {
        int id = getNextId(provider);
        SafeLocation location = JSONDataManager.GSON.fromJson(json.get("location"), SafeLocation.class);
        String title = json.get("title").getAsString();
        ChatColor color = ChatColor.valueOf(json.get("color").getAsString());
        int entryCount = json.get("entryCount").getAsInt();
        float scale = json.has("scale") ? json.get("scale").getAsFloat() : Leaderboard.DEFAULT_SCALE;
        UUID textUUID = UUID.fromString(json.get("textUUID").getAsString());
        return new Leaderboard(id, location, provider, title, color, entryCount, scale, textUUID);
    }
}
