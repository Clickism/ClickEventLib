package me.clickism.clickeventlib.trigger;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A trigger that has an implemented exit method that does nothing.
 */
public abstract class EnterOnlyTrigger extends Trigger {
    /**
     * Creates a new trigger with the given name.
     *
     * @param name name
     */
    public EnterOnlyTrigger(String name) {
        super(name);
    }

    @Override
    public void onExit(Player player, @Nullable TriggerBox box) {
        // Do nothing
    }
}
