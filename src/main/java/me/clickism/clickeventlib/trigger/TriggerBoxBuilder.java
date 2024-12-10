package me.clickism.clickeventlib.trigger;

import org.bukkit.Location;

/**
 * TriggerBoxBuilder class.
 */
public class TriggerBoxBuilder {
    private final Trigger trigger;
    private final int z;
    private Location pos1;
    private Location pos2;

    /**
     * Creates a new TriggerBoxBuilder with the given Trigger and z coordinate.
     *
     * @param trigger the trigger
     * @param z       the z coordinate
     */
    public TriggerBoxBuilder(Trigger trigger, int z) {
        this.trigger = trigger;
        this.z = z;
    }

    /**
     * Build the TriggerBox.
     *
     * @param id        the ID of the TriggerBox
     * @param worldName the name of the world
     * @return the built TriggerBox
     * @throws IllegalStateException if the TriggerBox has missing fields
     */
    public TriggerBox build(int id, String worldName) {
        if (!canBuild()) {
            throw new IllegalStateException("Can't build TriggerBox: pos1 and/or pos2 is not set");
        }
        return new TriggerBox(id, worldName, trigger, z, pos1.toVector().toBlockVector(), pos2.toVector().toBlockVector());
    }

    /**
     * Check if the TriggerBox has all the necessary fields to be built.
     *
     * @return true if the TriggerBox can be built, false otherwise
     */
    public boolean canBuild() {
        return pos1 != null && pos2 != null;
    }

    /**
     * Set the first position of the TriggerBox.
     *
     * @param pos1 first position
     * @return this TriggerBoxBuilder
     */
    public TriggerBoxBuilder setPos1(Location pos1) {
        this.pos1 = pos1;
        return this;
    }

    /**
     * Set the second position of the TriggerBox.
     *
     * @param pos2 second position
     * @return this TriggerBoxBuilder
     */
    public TriggerBoxBuilder setPos2(Location pos2) {
        this.pos2 = pos2;
        return this;
    }

    /**
     * Get the Trigger of the TriggerBox.
     *
     * @return the trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Get the z coordinate of the TriggerBox.
     *
     * @return the z coordinate
     */
    public int getZ() {
        return z;
    }
}
