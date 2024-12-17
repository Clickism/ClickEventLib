package me.clickism.clickeventlib.trigger;

import me.clickism.subcommandapi.util.Named;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

/**
 * TriggerBox class, used for triggering Triggers when players enter/exit the box.
 */
public class TriggerBox implements Named {
    private final String worldName;

    private final Trigger trigger;
    private final String name;
    private final int id;

    private final int maxX;
    private final int minX;
    private final int maxY;
    private final int minY;
    private final int maxZ;
    private final int minZ;

    private final int z;

    /**
     * Creates a new TriggerBox with the given Trigger.
     * Increases the bounds by 1 to include the block at the second position.
     *
     * @param id        ID
     * @param worldName world name
     * @param trigger   trigger
     * @param z         z-coordinate
     * @param pos1      first position
     * @param pos2      second position
     */
    public TriggerBox(int id, String worldName, @NotNull Trigger trigger, int z, BlockVector pos1, BlockVector pos2) {
        this(id, worldName, trigger, z,
                Math.min(pos1.getBlockX(), pos2.getBlockX()),
                Math.min(pos1.getBlockY(), pos2.getBlockY()),
                Math.min(pos1.getBlockZ(), pos2.getBlockZ()),
                Math.max(pos1.getBlockX(), pos2.getBlockX()) + 1,
                Math.max(pos1.getBlockY(), pos2.getBlockY()) + 1,
                Math.max(pos1.getBlockZ(), pos2.getBlockZ()) + 1);
    }

    /**
     * Creates a new TriggerBox with the given Trigger.
     *
     * @param id        ID
     * @param worldName world name
     * @param trigger   trigger
     * @param z         z-coordinate
     * @param minX      min x
     * @param minY      min y
     * @param minZ      min z
     * @param maxX      max x
     * @param maxY      max y
     * @param maxZ      max z
     */
    public TriggerBox(int id, String worldName, @NotNull Trigger trigger, int z, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.id = id;
        this.worldName = worldName;
        this.trigger = trigger;
        this.name = trigger.getName() + "_" + id;
        this.z = z;
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        this.maxZ = maxZ;
        this.minZ = minZ;
    }

    /**
     * Gets the ID of this TriggerBox.
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the world name of this TriggerBox.
     *
     * @return world name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Gets the z-coordinate of this TriggerBox.
     *
     * @return z-coordinate
     */
    public int getZ() {
        return z;
    }

    /**
     * Checks if the given location is inside this box.
     *
     * @param location location
     * @return true if inside, false otherwise
     */
    public boolean isInside(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return minX <= x && x < maxX &&
                minY <= y && y < maxY &&
                minZ <= z && z < maxZ;
    }

    /**
     * Triggers the enter Trigger for the given player.
     *
     * @param player player
     */
    public void enter(Player player) {
        trigger.onEnter(player, this);
    }

    /**
     * Triggers the exit Trigger for the given player.
     *
     * @param player player
     */
    public void exit(Player player) {
        trigger.onExit(player, this);
    }

    /**
     * Triggers the teleport exit Trigger for the given player.
     *
     * @param player player
     */
    public void teleportExit(Player player) {
        trigger.onTeleportExit(player, this);
    }

    /**
     * Gets the Trigger of this TriggerBox.
     *
     * @return trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Gets the minimum position of this box.
     *
     * @return min position
     */
    public BlockVector getMinPos() {
        return new BlockVector(minX, minY, minZ);
    }

    /**
     * Gets the maximum position of this box.
     *
     * @return max position
     */
    public BlockVector getMaxPos() {
        return new BlockVector(maxX, maxY, maxZ);
    }

    /**
     * Gets the closest distance from this box to the given location.
     *
     * @param location location
     * @return distance
     */
    public double distanceTo(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double closestX = clamp(x, minX, maxX);
        double closestY = clamp(y, minY, maxY);
        double closestZ = clamp(z, minZ, maxZ);
        double dx = x - closestX;
        double dy = y - closestY;
        double dz = z - closestZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public String getName() {
        return name;
    }
}
