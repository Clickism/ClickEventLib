package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.phase.PhaseManager;
import me.clickism.clickeventlib.phase.group.PhaseGroup;
import me.clickism.subcommandapi.argument.Argument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.argument.TimeArgument;
import me.clickism.subcommandapi.command.ArgumentHandler;
import me.clickism.subcommandapi.command.CommandException;
import me.clickism.subcommandapi.command.CommandResult;
import me.clickism.subcommandapi.command.CommandStack;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

class PhaseStartSubcommand extends PhaseSubcommand {

    private static final String RAW_FLAG = "raw";
    private static final String FORCE_FLAG = "force";

    private static final TimeArgument TIME_ARGUMENT =
            new TimeArgument("timer", false);

    private final SelectionArgument<PhaseGroup> phaseGroupArgument;
    private final SelectionArgument<Phase> phaseArgument;

    private final PhaseManager phaseManager;

    public PhaseStartSubcommand(PhaseManager phaseManager) {
        super("start", true);
        this.phaseManager = phaseManager;
        this.phaseGroupArgument = new SelectionArgument<>("phase_group", true, phaseManager.getPhaseGroups());
        this.phaseArgument = new SelectionArgument<>("phase", true, phaseManager.getPhasesInCurrentGroup());
        addArgument(phaseGroupArgument);
        addArgument(phaseArgument);
        addArgument(TIME_ARGUMENT);
        addFlag(RAW_FLAG);
    }

    @Override
    public List<String> getTabCompletion(int index, CommandSender sender, String[] args) {
        List<Argument<?>> arguments = getArguments();
        if (index < arguments.size() && arguments.get(index).equals(phaseArgument)) {
            PhaseGroup group = phaseGroupArgument.parse(sender, args[index - 1]);
            return group.getPhases().stream()
                    .map(Phase::getName)
                    .collect(Collectors.toList());
        }
        return super.getTabCompletion(index, sender, args);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        PhaseGroup group = argHandler.get(phaseGroupArgument);
        Phase phase = argHandler.getOrNull(phaseArgument);
        Long timer = argHandler.getOrNull(TIME_ARGUMENT);
        boolean raw = argHandler.hasFlag(RAW_FLAG);
        if (!argHandler.hasFlag(FORCE_FLAG)) {
            validateEventLocations(group.getRequiredEventLocations());
        }
        phaseManager.setPhaseGroup(group);
        String message = "Started phase group &l" + group.getName();
        if (phase != null) {
            if (raw) {
                phaseManager.setPhase(phase);
            } else {
                phaseManager.startPhase(phase);
            }
            message += "&a at phase &l" + phase.getName();
        }
        if (timer != null) {
            phaseManager.setSecondsRemaining(timer);
            message += "&a with timer at &l" + timer + " seconds";
        }
        return CommandResult.success(message + ".");
    }

}
