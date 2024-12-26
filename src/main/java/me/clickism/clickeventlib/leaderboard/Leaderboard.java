package me.clickism.clickeventlib.leaderboard;

import me.clickism.clickeventlib.location.SafeLocation;
import me.clickism.clickeventlib.statistic.UUIDManager;
import me.clickism.clickeventlib.util.DisplayHandler;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a leaderboard that displays entries from a leaderboard entry provider.
 */
public class Leaderboard extends DisplayHandler implements Named {
    /**
     * The default number of entries to display on the leaderboard.
     */
    public static final int DEFAULT_ENTRY_COUNT = 10;
    /**
     * The default scale of the leaderboard text.
     */
    public static final float DEFAULT_SCALE = 1f;

    private final LeaderboardEntryProvider provider;

    private final int id;

    private final SafeLocation safeLocation;
    private final String name;
    private final String title;
    private final ChatColor color;
    private final int entryCount;
    private final float scale;

    private UUID textUUID;

    /**
     * Creates a new leaderboard.
     *
     * @param id         ID
     * @param location   location
     * @param provider   provider
     * @param title      title
     * @param color      color
     * @param entryCount entry count
     * @param scale      scale
     */
    public Leaderboard(int id, SafeLocation location, LeaderboardEntryProvider provider, String title,
                       ChatColor color, int entryCount, float scale) {
        this(id, location, provider, title, color, entryCount, scale, null);
    }

    /**
     * Creates a new leaderboard with a pre-existing text display entity.
     *
     * @param id         ID
     * @param location   location
     * @param provider   provider
     * @param title      title
     * @param color      color
     * @param entryCount entry count
     * @param scale      scale
     * @param textUUID   text display entity UUID
     */
    public Leaderboard(int id, SafeLocation location, LeaderboardEntryProvider provider, String title,
                       ChatColor color, int entryCount, float scale, UUID textUUID) {
        this.id = id;
        this.safeLocation = location;
        this.provider = provider;
        this.name = getName(provider, id);
        this.title = title;
        this.color = color;
        this.entryCount = entryCount;
        this.scale = scale;
        this.textUUID = textUUID;
    }

    /**
     * Gets the ID of the leaderboard.
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Updates the leaderboard.
     */
    public void update() {
        Location location = safeLocation.getLocation();
        if (location == null) return; // World not loaded
        try {
            this.textUUID = spawnDisplayIfNotExists(textUUID, location, EntityType.TEXT_DISPLAY);
        } catch (IllegalArgumentException ignored) {
            return;
        }
        TextDisplay display = getTextDisplay(textUUID);
        if (display == null) return;
        display.setBillboard(Display.Billboard.FIXED);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        Transformation transformation = display.getTransformation();
        display.setTransformation(applyScale(transformation, scale));
        String text = getText();
        display.setText(text);
    }

    /**
     * Removes the leaderboard.
     */
    public void remove() {
        removeEntityIfExists(textUUID);
    }

    /**
     * Applies a scale to a transformation.
     *
     * @param transformation transformation
     * @param scale          scale
     * @return scaled transformation
     */
    private static Transformation applyScale(Transformation transformation, float scale) {
        return new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                new Vector3f(scale, scale, scale),
                transformation.getRightRotation()
        );
    }

    /**
     * Gets the text to display on the leaderboard.
     *
     * @return text
     */
    private String getText() {
        StringBuilder sb = new StringBuilder(title);
        List<Map.Entry<UUID, String>> entries = provider.getLeaderboardEntries();
        int size = Math.min(entries.size(), entryCount);
        entries.subList(0, size).forEach(entry -> {
            UUID uuid = entry.getKey();
            String name = UUIDManager.getName(uuid);
            if (name == null) {
                name = "Unknown";
            }
            String value = entry.getValue();
            sb.append("\n").append(color).append(name).append(": ").append(ChatColor.WHITE).append(value);
        });
        return sb.toString();
    }

    /**
     * Gets the leaderboard entry provider.
     *
     * @return provider
     */
    public LeaderboardEntryProvider getProvider() {
        return provider;
    }

    /**
     * Gets the safe location of the leaderboard.
     *
     * @return safe location
     */
    public SafeLocation getSafeLocation() {
        return safeLocation;
    }

    /**
     * Gets the title of the leaderboard.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the color of the leaderboard.
     *
     * @return color
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Gets the maximum number of entries to display on the leaderboard.
     *
     * @return entry count
     */
    public int getEntryCount() {
        return entryCount;
    }

    /**
     * Gets the scale of the leaderboard text display.
     *
     * @return scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Gets the UUID of the text display entity.
     *
     * @return text display entity UUID
     */
    public UUID getTextUUID() {
        return textUUID;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the name of a leaderboard with a given ID.
     *
     * @param provider provider
     * @param id       ID
     * @return name
     */
    public static String getName(LeaderboardEntryProvider provider, int id) {
        return provider.getName() + "_" + id;
    }
}
