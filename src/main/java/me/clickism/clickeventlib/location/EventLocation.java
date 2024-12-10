package me.clickism.clickeventlib.location;

import me.clickism.clickeventlib.util.Named;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents a named location in the world that needs to be set in-game.
 */
public class EventLocation implements Named {
    private final String name;
    private SafeLocation safeLocation;

    /**
     * Create a new event location with the given name.
     *
     * @param name the name of this event location.
     */
    public EventLocation(String name) {
        this.name = name;
    }

    /**
     * Get the key of this event location.
     *
     * @return the key of this event location.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the location of this event location.
     *
     * @return the location of this event location, or null if not set yet, or if the world is not loaded.
     */
    @Nullable
    public Location getLocation() {
        if (safeLocation == null) return null;
        return safeLocation.getLocation();
    }

    /**
     * Get the safe location of this event location.
     *
     * @return the safe location of this event location, or null if not set yet.
     */
    @Nullable
    public SafeLocation getSafeLocation() {
        return safeLocation;
    }

    /**
     * Check if the location of this event location is set.
     *
     * @return true if the location of this event location is set.
     */
    public boolean isLocationSet() {
        return safeLocation != null;
    }

    /**
     * Check if the world of this event location is loaded.
     *
     * @return true if the world of this event location is loaded.
     */
    public boolean isWorldLoaded() {
        return safeLocation != null && safeLocation.isWorldLoaded();
    }

    /**
     * Set the location of this event location.
     *
     * @param worldName the name of the world of this event location.
     * @param location  the location of this event location.
     */
    public void setLocation(String worldName, Location location) {
        this.safeLocation = new SafeLocation(worldName, location);
    }

    /**
     * Set the safe location of this event location.
     *
     * @param safeLocation the safe location of this event location.
     */
    public void setSafeLocation(SafeLocation safeLocation) {
        this.safeLocation = safeLocation;
    }

    /**
     * Teleport the given players to this event location if it is set.
     *
     * @param players the players to teleport.
     * @return true if the players were teleported, false otherwise.
     */
    public boolean teleportIfSet(Collection<? extends Player> players) {
        Location location = getLocation();
        if (location == null) return false;
        players.forEach(player -> player.teleport(location));
        return true;
    }

    /**
     * Teleport the given player to this event location if it is set.
     *
     * @param player the player to teleport.
     * @return true if the player was teleported, false otherwise.
     */
    public boolean teleportIfSet(Player player) {
        Location location = getLocation();
        if (location == null) return false;
        player.teleport(location);
        return true;
    }

    /**
     * Smoothly teleport the given player to this event location if it is set.
     *
     * @param player the player to teleport.
     * @param plugin the plugin to run the task on.
     * @return true if the player was teleported, false otherwise.
     */
    public boolean teleportSmoothlyIfSet(Player player, Plugin plugin) {
        return teleportSmoothlyIfSet(player, plugin, () -> {});
    }

    /**
     * Smoothly teleport the given player to this event location if it is set and run the given task after teleporting.
     * The task won't run if the player wasn't teleported.
     *
     * @param player     the player to teleport.
     * @param plugin     the plugin to run the task on.
     * @param onTeleport the task to run after teleporting.
     * @return true if the player was teleported, false otherwise.
     */
    public boolean teleportSmoothlyIfSet(Player player, Plugin plugin, Runnable onTeleport) {
        Location location = getLocation();
        if (location == null) return false;
        Utils.teleportSmoothly(player, location, plugin, onTeleport);
        return true;
    }

    /**
     * Teleport the given players to this event location if it is set.
     *
     * @param players the players to teleport.
     * @param plugin  the plugin to run the task on.
     * @return true if the players were teleported, false otherwise.
     */
    public boolean teleportSmoothlyIfSet(Collection<? extends Player> players, Plugin plugin) {
        return teleportSmoothlyIfSet(players, plugin, player -> {});
    }

    /**
     * Smoothly teleport the given players to this event location if it is set and run the given task after teleporting.
     * The task won't run if the players weren't teleported.
     *
     * @param players    the players to teleport
     * @param plugin     the plugin to run the task on
     * @param onTeleport the task to run after teleporting
     * @return true if the players were teleported, false otherwise
     */
    public boolean teleportSmoothlyIfSet(Collection<? extends Player> players, Plugin plugin, Consumer<Player> onTeleport) {
        Location location = getLocation();
        if (location == null) return false;
        Utils.teleportSmoothly(players, location, plugin, onTeleport);
        return true;
    }
}
