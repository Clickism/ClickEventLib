package me.clickism.clickeventlib.phase.group;

import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A group of phases that loops back to the first phase when the last phase is reached.
 */
public class PhaseLoop extends PhaseQueue {
    /**
     * Creates a new phase loop.
     *
     * @param identifier the identifier of the phase loop
     * @param phases     the phases
     */
    protected PhaseLoop(Identifier identifier, List<Phase> phases) {
        super(identifier, phases);
    }

    @Override
    public @Nullable Phase getNextPhase() {
        Phase nextPhase = super.getNextPhase();
        if (nextPhase == null) {
            nextIndex = 0;
            nextPhase = super.getNextPhase();
        }
        return nextPhase;
    }

    /**
     * Creates a phase loop with the given identifier and phases.
     * Phases will be run in the order they are given.
     *
     * @param identifier the identifier of the phase loop
     * @param phases     the phases
     * @return the phase loop
     */
    public static PhaseLoop of(Identifier identifier, Phase... phases) {
        return new PhaseLoop(identifier, List.of(phases));
    }

    /**
     * Creates a phase loop with the given identifier and phases.
     * Phases will be run in the order of the list.
     *
     * @param identifier the identifier of the phase loop
     * @param phases     the phases
     * @return the phase loop
     */
    public static PhaseLoop of(Identifier identifier, List<Phase> phases) {
        return new PhaseLoop(identifier, Collections.unmodifiableList(phases));
    }
}
