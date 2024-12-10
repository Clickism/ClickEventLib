package me.clickism.clickeventlib.debug;

import me.clickism.clickeventlib.util.DisplayHandler;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Display locations for debugging purposes.
 */
public class LocationDisplayer extends DisplayHandler {
    private static final Transformation COVER_TRANSFORMATION = new Transformation(
            new Vector3f(-.505f, -.005f, -.505f), new AxisAngle4f(0f, 0f, 0f, 0f),
            new Vector3f(1.01f, 1.01f, 1.01f), new AxisAngle4f(0f, 0f, 0f, 0f));

    private static final List<Entity> GLOBAL_DISPLAYS = new ArrayList<>();

    private final List<Entity> displays = new ArrayList<>();

    /**
     * Create a new location displayer.
     */
    public LocationDisplayer() {
    }

    /**
     * Display a location with a material.
     *
     * @param location the location to display
     * @param material the material to display
     * @param color    the color of the material
     * @param text     the text to display
     */
    public void displayLocation(Location location, Material material, Color color, String text) {
        if (location.getWorld() == null) return;
        location = location.getBlock().getLocation().add(.5, 0, .5);
        BlockDisplay display = buildBlockDisplay(location, material);
        display.setGlowing(true);
        display.setGlowColorOverride(color);
        addDisplay(display);
        Location textLocation = location.clone().add(0, 1.25, 0);
        TextDisplay textDisplay = buildTextDisplay(textLocation, text);
        addDisplay(textDisplay);
    }

    /**
     * Display a box with a material.
     *
     * @param min      the minimum corner of the box
     * @param max      the maximum corner of the box
     * @param material the material to display
     * @param text     the text to display
     */
    public void displayBox(Location min, Location max, Material material, String text) {
        if (min.getWorld() == null) return;
        BlockDisplay display = buildBlockDisplay(min, material);
        float xScale = max.getBlockX() - min.getBlockX();
        float yScale = max.getBlockY() - min.getBlockY();
        float zScale = max.getBlockZ() - min.getBlockZ();
        display.setTransformation(new Transformation(
                new Vector3f(0f, 0f, 0f),
                new AxisAngle4f(0f, 0f, 0f, 0f),
                new Vector3f(xScale, yScale, zScale),
                new AxisAngle4f(0f, 0f, 0f, 0f)
        ));
        addDisplay(display);
        Location center = min.clone().add(xScale / 2, yScale / 2, zScale / 2);
        TextDisplay textDisplay = buildTextDisplay(center, text);
        addDisplay(textDisplay);
    }

    private void addDisplay(Entity entity) {
        displays.add(entity);
        GLOBAL_DISPLAYS.add(entity);
    }

    /**
     * Check if the displayer is displaying anything.
     *
     * @return true if displaying
     */
    public boolean isDisplaying() {
        return !displays.isEmpty();
    }

    /**
     * Remove all displays.
     */
    public void removeDisplays() {
        displays.forEach(Entity::remove);
        displays.clear();
    }

    /**
     * Remove all displays.
     */
    public static void removeGlobalDisplays() {
        GLOBAL_DISPLAYS.forEach(Entity::remove);
        GLOBAL_DISPLAYS.clear();
    }

    private static TextDisplay buildTextDisplay(Location location, String text) {
        TextDisplay display = spawnTextDisplay(location);
        display.setText(text);
        display.setBillboard(Display.Billboard.VERTICAL);
        display.setViewRange(4f);
        return display;
    }

    private static BlockDisplay buildBlockDisplay(Location location, Material material) {
        BlockDisplay display = spawnBlockDisplay(location);
        display.setTransformation(COVER_TRANSFORMATION);
        display.setBlock(material.createBlockData());
        display.setBrightness(new Display.Brightness(15, 15));
        display.setViewRange(4f);
        return display;
    }
}
