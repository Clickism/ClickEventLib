package me.clickism.clickeventlib.phase.group;

import me.clickism.clickeventlib.phase.StartScript;
import org.jetbrains.annotations.NotNull;

public abstract class PhaseBase implements PhaseGroup {
    protected StartScript startScript = () -> {};

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
