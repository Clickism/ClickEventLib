package me.clickism.clickeventlib.phase.group;

import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.phase.StartScript;
import me.clickism.clickeventlib.util.Identifier;
import me.clickism.subcommandapi.util.Named;
import me.clickism.subcommandapi.util.NamedCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A group of phases.
 */
public interface PhaseGroup extends Named {
    /**
     * Gets the next phase in the group.
     *
     * @return the next phase, or null if there are no more phases
     */
    @Nullable Phase getNextPhase();

    /**
     * Sets the current phase for the phase group.
     *
     * @param phase the current phase
     * @throws IllegalArgumentException if the phase is not found in the group
     */
    void setCurrentPhase(Phase phase) throws IllegalArgumentException;

    /**
     * Gets the phases in the group.
     *
     * @return the phases
     */
    NamedCollection<Phase> getPhases();

    /**
     * Adds a start script to the phase group.
     *
     * @param startScript the start script
     * @return the phase group
     */
    PhaseGroup withStartScript(StartScript startScript);

    /**
     * Gets the start script for the phase group.
     *
     * @return the start script
     */
    @NotNull StartScript getStartScript();

    /**
     * Gets the required event locations for the group.
     *
     * @return the required event locations
     */
    default List<EventLocation> getRequiredEventLocations() {
        return getPhases().stream()
                .map(Phase::getRequiredEventLocations)
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

    /**
     * Creates a phase group with a single phase with the identifier of the phase.
     *
     * @param phase the phase
     * @return the phase group
     */
    static PhaseGroup ofSingle(Phase phase) {
        return ofSingle(phase.getId(), phase);
    }

    /**
     * Creates a phase group with a single phase with the given identifier.
     *
     * @param identifier the identifier of the phase group
     * @param phase      the phase
     * @return the phase group
     */
    static PhaseGroup ofSingle(Identifier identifier, Phase phase) {
        return new PhaseBase() {
            private boolean finished = false;
            private final NamedCollection<Phase> phases = new NamedCollection<>(List.of(phase));

            @Override
            public @Nullable Phase getNextPhase() {
                return finished ? null : phase;
            }

            @Override
            public void setCurrentPhase(Phase currentPhase) {
                if (!phase.equals(currentPhase)) {
                    throw new IllegalArgumentException("Phase not found in single phase group.");
                }
                finished = true;
            }

            @Override
            public NamedCollection<Phase> getPhases() {
                return phases;
            }

            @Override
            public String getName() {
                return identifier.toString();
            }
        };
    }
}
