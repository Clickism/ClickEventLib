package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.phase.PhaseManager;
import me.clickism.subcommandapi.command.SubcommandGroup;

/**
 * Subcommand group for phase-related commands.
 */
public class PhaseSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new phase subcommand group.
     *
     * @param phaseManager the phase manager
     */
    public PhaseSubcommandGroup(PhaseManager phaseManager) {
        super("phase", true);
        addSubcommand(new PhaseSetSubcommand(phaseManager));
        addSubcommand(new PhaseTimerSubcommand(phaseManager));
        addSubcommand(new PhaseSkipSubcommand(phaseManager));
    }
}
