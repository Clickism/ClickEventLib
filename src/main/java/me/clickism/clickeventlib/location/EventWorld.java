package me.clickism.clickeventlib.location;

import me.clickism.subcommandapi.util.Named;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a world that is used in events.
 */
public abstract class EventWorld implements Named {
    private final String name;

    /**
     * Creates a new EventWorld with the given name.
     *
     * @param name the name of the world
     */
    public EventWorld(String name) {
        this.name = name;
    }

    /**
     * Gets the world with the name of this EventWorld.
     *
     * @return the world, or null if the world is not loaded
     */
    @Nullable
    public World getWorld() {
        return Bukkit.getWorld(name);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Checks if the player is inside this event world.
     *
     * @param player the player
     * @return true if the player is inside this world, false otherwise
     */
    public boolean isInside(Player player) {
        return isInside(player.getLocation());
    }

    /**
     * Checks if the location is inside this event world.
     *
     * @param location the location
     * @return true if the location is inside this world, false otherwise
     */
    public boolean isInside(Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        return world.getName().equals(name);
    }

    /**
     * Sets up the world by applying the necessary gamerules and other settings.
     * <p>
     * Make sure the world is loaded before calling this method.
     *
     * @throws IllegalStateException if the world is not loaded
     */
    public void setupWorld() {
        World world = getWorld();
        if (world == null) {
            throw new IllegalStateException("World " + name + " is not loaded.");
        }
        setupGamerules(world);
    }

    /**
     * Sets up the gamerules for the world.
     *
     * @param world the world
     */
    protected abstract void setupGamerules(World world);
}
