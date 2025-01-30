package me.clickism.clickeventlib.phase.group;

import me.clickism.clickeventlib.phase.StartScript;
import org.jetbrains.annotations.NotNull;

/**
 * A base class for phase groups.
 */
public abstract class PhaseBase implements PhaseGroup {
    /**
     * The start script for the phase group.
     */
    protected StartScript startScript = () -> {};

    /**
     * Creates a new phase group.
     */
    public PhaseBase() {
    }

    @Override
    public PhaseGroup withStartScript(StartScript startScript) {
        this.startScript = startScript;
        return this;
    }

    @Override
    public @NotNull StartScript getStartScript() {
        return startScript;
    }
}
