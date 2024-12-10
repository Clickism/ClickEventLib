package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.TimeArgument;
import me.clickism.clickeventlib.phase.PhaseManager;
import org.bukkit.command.CommandSender;

class PhaseTimerSubcommand extends Subcommand {
    private static final TimeArgument TIME_ARGUMENT = new TimeArgument("time", true);

    private final PhaseManager phaseManager;

    public PhaseTimerSubcommand(PhaseManager phaseManager) {
        super("timer", true);
        this.phaseManager = phaseManager;
        addArgument(TIME_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Long time = argHandler.get(TIME_ARGUMENT);
        phaseManager.setSecondsRemaining(time);
        return CommandResult.success("Phase timer set to &l" + time + " seconds.");
    }
}
