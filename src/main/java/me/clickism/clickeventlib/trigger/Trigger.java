package me.clickism.clickeventlib.trigger;

import me.clickism.clickeventlib.util.Named;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Trigger class.
 */
public abstract class Trigger implements Named {
    private final String name;

    /**
     * Creates a new trigger with the given name.
     *
     * @param name name
     */
    public Trigger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Triggered when entering a trigger box.
     *
     * @param player player entering the trigger box
     * @param box    trigger box entered
     */
    public abstract void onEnter(Player player, @Nullable TriggerBox box);

    /**
     * Triggered when exiting a trigger box.
     *
     * @param player player exiting the trigger box
     * @param box    trigger box exited
     */
    public abstract void onExit(Player player, @Nullable TriggerBox box);

    /**
     * Triggered when a player is teleported out of a trigger box.
     * @param player player teleported out
     * @param box trigger box teleported out of
     */
    public void onTeleportExit(Player player, @Nullable TriggerBox box) {
        // Do nothing by default
    }
}
