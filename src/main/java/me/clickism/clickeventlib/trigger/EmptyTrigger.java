package me.clickism.clickeventlib.trigger;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A trigger that does nothing when a player enters or exits it.
 */
public class EmptyTrigger extends Trigger {
    /**
     * Creates a new trigger with the given name.
     *
     * @param name name
     */
    public EmptyTrigger(String name) {
        super(name);
    }

    @Override
    public void onEnter(Player player, @Nullable TriggerBox box) {
        // Do nothing
    }

    @Override
    public void onExit(Player player, @Nullable TriggerBox box) {
        // Do nothing
    }
}
