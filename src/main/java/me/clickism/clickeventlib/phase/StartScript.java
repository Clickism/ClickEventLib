package me.clickism.clickeventlib.phase;

/**
 * Represents a start script that gets run when a {@link me.clickism.clickeventlib.phase.group.PhaseGroup} starts.
 */
@FunctionalInterface
public interface StartScript {
    /**
     * Starts the script.
     */
    void start();
}
