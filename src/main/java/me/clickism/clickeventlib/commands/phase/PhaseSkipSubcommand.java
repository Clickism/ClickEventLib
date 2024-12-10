package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.phase.PhaseManager;
import org.bukkit.command.CommandSender;

class PhaseSkipSubcommand extends Subcommand {
    private final PhaseManager phaseManager;

    public PhaseSkipSubcommand(PhaseManager phaseManager) {
        super("skip", true);
        this.phaseManager = phaseManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Phase phase = phaseManager.startNextPhase();
        if (phase == null) {
            return CommandResult.failure("No more phases to skip.");
        }
        return CommandResult.success("Skipped to the next phase: &l" + phase.getName());
    }
}
