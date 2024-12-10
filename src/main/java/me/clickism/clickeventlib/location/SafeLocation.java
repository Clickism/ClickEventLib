package me.clickism.clickeventlib.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a location that can be used/saved/loaded without its world being loaded.
 */
public final class SafeLocation {
    private final @NotNull String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;


    /**
     * Creates a new safe location from the given world name and coordinates.
     *
     * @param worldName the name of the world
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param z         the z coordinate
     * @param yaw       the yaw
     * @param pitch     the pitch
     */
    public SafeLocation(@NotNull String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = Objects.requireNonNull(worldName);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Creates a new safe location from the given location.
     * The world of this location must not be null.
     *
     * @param location the location
     */
    public SafeLocation(@NotNull Location location) {
        this(Objects.requireNonNull(location.getWorld()).getName(), location);
    }

    /**
     * Creates a new safe location from the given world name and location.
     *
     * @param worldName the name of the world
     * @param location  the location
     */
    public SafeLocation(@NotNull String worldName, @NotNull Location location) {
        this(worldName, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Get the location of this safe location.
     *
     * @return the location, or null if the world is not loaded.
     */
    @Nullable
    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Check if the world of this safe location is loaded.
     *
     * @return true if the world is loaded, false otherwise
     */
    public boolean isWorldLoaded() {
        return Bukkit.getWorld(worldName) != null;
    }

    /**
     * Get the name of the world.
     *
     * @return the name of the world
     */
    @NotNull
    public String getWorldName() {
        return worldName;
    }

    /**
     * Get the x coordinate.
     *
     * @return the x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y coordinate.
     *
     * @return the y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Get the z coordinate.
     *
     * @return the z coordinate
     */
    public double getZ() {
        return z;
    }

    /**
     * Get the yaw.
     *
     * @return the yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Get the pitch.
     *
     * @return the pitch
     */
    public float getPitch() {
        return pitch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        SafeLocation that = (SafeLocation) obj;
        return Objects.equals(this.worldName, that.worldName) &&
                Double.doubleToLongBits(this.x) == Double.doubleToLongBits(that.x) &&
                Double.doubleToLongBits(this.y) == Double.doubleToLongBits(that.y) &&
                Double.doubleToLongBits(this.z) == Double.doubleToLongBits(that.z) &&
                Float.floatToIntBits(this.yaw) == Float.floatToIntBits(that.yaw) &&
                Float.floatToIntBits(this.pitch) == Float.floatToIntBits(that.pitch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, x, y, z, yaw, pitch);
    }
}
