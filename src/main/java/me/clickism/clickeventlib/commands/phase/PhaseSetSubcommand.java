package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.phase.PhaseManager;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.argument.TimeArgument;
import me.clickism.subcommandapi.command.ArgumentHandler;
import me.clickism.subcommandapi.command.CommandException;
import me.clickism.subcommandapi.command.CommandResult;
import me.clickism.subcommandapi.command.CommandStack;
import org.bukkit.command.CommandSender;

class PhaseSetSubcommand extends PhaseSubcommand {
    private static final String RAW_FLAG = "raw";
    private static final String FORCE_FLAG = "force";

    private static final TimeArgument TIME_ARGUMENT = new TimeArgument("time", false);

    private final PhaseManager phaseManager;
    private final SelectionArgument<Phase> phaseArgument;

    public PhaseSetSubcommand(PhaseManager phaseManager) {
        super("set", true);
        this.phaseManager = phaseManager;
        this.phaseArgument = new SelectionArgument<>("phase", true, phaseManager::getPhasesInCurrentGroup);
        addArgument(phaseArgument);
        addArgument(TIME_ARGUMENT);
        addFlag(RAW_FLAG);
        addFlag(FORCE_FLAG);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Phase phase = argHandler.get(phaseArgument);
        Long time = argHandler.getOrNull(TIME_ARGUMENT);
        if (!argHandler.hasFlag(FORCE_FLAG)) {
            validateEventLocations(phase.getRequiredEventLocations());
        }
        if (argHandler.hasFlag(RAW_FLAG)) {
            phaseManager.setPhase(phase);
        } else {
            phaseManager.startPhase(phase);
        }
        if (time != null) {
            phaseManager.setSecondsRemaining(time);
            return CommandResult.success("Phase set to &l" + phase.getName() + "&a with timer at &l" + time + " seconds.");
        } else {
            return CommandResult.success("Phase set to &l" + phase.getName());
        }
    }
}
