package me.clickism.clickeventlib.phase.group;

import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.util.Identifier;
import me.clickism.subcommandapi.util.NamedCollection;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * A group of phases that executes phases in order, one after the other.
 */
public class PhaseQueue implements PhaseGroup {

    private final Identifier identifier;
    private final NamedCollection<Phase> namedPhases;
    private final List<Phase> phases;
    /**
     * The index of the next phase in the queue.
     */
    protected int nextIndex = 0;

    /**
     * Creates a new phase queue.
     *
     * @param identifier the identifier of the phase queue
     * @param phases     the phases
     */
    protected PhaseQueue(Identifier identifier, List<Phase> phases) {
        this.identifier = identifier;
        this.phases = phases;
        this.namedPhases = new NamedCollection<>(phases);
    }

    @Override
    public @Nullable Phase getNextPhase() {
        if (phases.isEmpty()) return null;
        if (nextIndex >= phases.size()) {
            return null;
        }
        return phases.get(nextIndex);
    }

    @Override
    public void setCurrentPhase(Phase phase) {
        if (phases.isEmpty()) return;
        if (phase.equals(getNextPhase())) {
            // Phase is next in queue, so just increment index
            nextIndex++;
            return;
        }
        int index = phases.indexOf(phase);
        if (index == -1) {
            throw new IllegalArgumentException("Phase not found in loop");
        }
        nextIndex = index + 1;
    }

    @Override
    public NamedCollection<Phase> getPhases() {
        return namedPhases;
    }

    @Override
    public String getName() {
        return identifier.toString();
    }

    /**
     * Creates a phase queue with the given identifier and phases.
     * Phases will be run in the order they are given.
     *
     * @param identifier the identifier of the phase queue
     * @param phases     the phases
     * @return the phase queue
     */
    public static PhaseQueue of(Identifier identifier, Phase... phases) {
        return new PhaseQueue(identifier, List.of(phases));
    }

    /**
     * Creates a phase queue with the given identifier and phases.
     * Phases will be run in the order of the list.
     *
     * @param identifier the identifier of the phase queue
     * @param phases     the phases
     * @return the phase queue
     */
    public static PhaseQueue of(Identifier identifier, List<Phase> phases) {
        return new PhaseQueue(identifier, Collections.unmodifiableList(phases));
    }
}
