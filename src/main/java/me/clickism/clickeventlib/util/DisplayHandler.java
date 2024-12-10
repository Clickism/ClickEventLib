package me.clickism.clickeventlib.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A utility class for handling display entities.
 */
public class DisplayHandler {
    /**
     * Constructs a new display handler.
     */
    public DisplayHandler() {
    }

    /**
     * Gets an item display by UUID.
     *
     * @param uuid the UUID of the item display
     * @return the item display
     */
    protected static ItemDisplay getItemDisplay(UUID uuid) {
        return (ItemDisplay) Bukkit.getEntity(uuid);
    }

    /**
     * Gets a block display by UUID.
     *
     * @param uuid the UUID of the block display
     * @return the block display
     */
    protected static BlockDisplay getBlockDisplay(UUID uuid) {
        return (BlockDisplay) Bukkit.getEntity(uuid);
    }

    /**
     * Gets a text display by UUID.
     *
     * @param uuid the UUID of the text display
     * @return the text display
     */
    protected static TextDisplay getTextDisplay(UUID uuid) {
        return (TextDisplay) Bukkit.getEntity(uuid);
    }

    /**
     * Removes an entity if it exists.
     *
     * @param uuid the UUID of the entity
     */
    protected static void removeEntityIfExists(@Nullable UUID uuid) {
        if (uuid == null) return;
        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) entity.remove();
    }

    /**
     * Spawns a block display.
     *
     * @param location the location to spawn the block display
     * @return the block display
     * @throws IllegalArgumentException if the world is null
     */
    protected static BlockDisplay spawnBlockDisplay(Location location) throws IllegalArgumentException {
        return (BlockDisplay) spawnDisplay(location, EntityType.BLOCK_DISPLAY);
    }

    /**
     * Spawns a text display.
     *
     * @param location the location to spawn the text display
     * @return the text display
     * @throws IllegalArgumentException if the world is null
     */
    protected static TextDisplay spawnTextDisplay(Location location) throws IllegalArgumentException {
        return (TextDisplay) spawnDisplay(location, EntityType.TEXT_DISPLAY);
    }

    /**
     * Spawns an item display.
     *
     * @param location the location to spawn the item display
     * @return the item display
     * @throws IllegalArgumentException if the world is null
     */
    protected static ItemDisplay spawnItemDisplay(Location location) throws IllegalArgumentException {
        return (ItemDisplay) spawnDisplay(location, EntityType.ITEM_DISPLAY);
    }

    /**
     * Spawns a display of a specified type.
     *
     * @param location the location to spawn the display
     * @param type     display entity type
     * @return display entity
     * @throws IllegalArgumentException if the world is null
     */
    protected static Display spawnDisplay(Location location, EntityType type) throws IllegalArgumentException {
        World world = location.getWorld();
        if (world == null) throw new IllegalArgumentException("World is null.");
        Display display = (Display) world.spawnEntity(location, type);
        display.setShadowRadius(0f);
        return display;
    }

    /**
     * Spawns a display entity if an entity with the specified UUID does not exist.
     *
     * @param uuid     the UUID of the entity
     * @param location the location to spawn the entity
     * @param type     the entity type
     * @return the UUID of the old entity if it exists, the UUID of the new entity otherwise
     * @throws IllegalArgumentException if the world is null
     */
    protected static UUID spawnDisplayIfNotExists(@Nullable UUID uuid, Location location, EntityType type) throws IllegalArgumentException {
        if (exists(uuid)) return uuid;
        return spawnDisplay(location, type).getUniqueId();
    }

    /**
     * Checks if an entity exists.
     *
     * @param uuid the UUID of the entity
     * @return true if the entity exists, false otherwise
     */
    protected static boolean exists(@Nullable UUID uuid) {
        return uuid != null && Bukkit.getEntity(uuid) != null;
    }
}
