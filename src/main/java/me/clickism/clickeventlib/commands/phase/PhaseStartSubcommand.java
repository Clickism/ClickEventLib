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
    private final FakeArgument phaseArgument;

    private final PhaseManager phaseManager;

    public PhaseStartSubcommand(PhaseManager phaseManager) {
        super("start", true);
        this.phaseManager = phaseManager;
        this.phaseGroupArgument = new SelectionArgument<>("phase_group", true, phaseManager.getPhaseGroups());
        this.phaseArgument = new FakeArgument("phase", false);
        addArgument(phaseGroupArgument);
        addArgument(phaseArgument);
        addArgument(TIME_ARGUMENT);
        addFlag(RAW_FLAG);
        addFlag(FORCE_FLAG);
    }

    @Override
    public List<String> getTabCompletion(int index, CommandSender sender, String[] args) {
        List<Argument<?>> arguments = getArguments();
        if (index < arguments.size() && arguments.get(index).equals(phaseArgument)) {
            try {
                PhaseGroup group = phaseGroupArgument.parse(sender, args[index - 1]);
                return group.getPhases().stream()
                        .map(Phase::getName)
                        .collect(Collectors.toList());
            } catch (CommandException e) {
                return List.of();
            }
        }
        return super.getTabCompletion(index, sender, args);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        PhaseGroup group = argHandler.get(phaseGroupArgument);
        Phase phase = parsePhase(group, argHandler);
        Long timer = argHandler.getOrNull(TIME_ARGUMENT);
        boolean raw = argHandler.hasFlag(RAW_FLAG);
        if (!argHandler.hasFlag(FORCE_FLAG)) {
            validateEventLocations(group.getRequiredEventLocations());
        }
        String message = "Started phase group &l" + group.getName();
        phaseManager.setPhaseGroup(group);
        if (phase != null && !phase.equals(group.getNextPhase())) {
            // Phase is not the first in the group
            if (raw) {
                phaseManager.setPhase(phase);
            } else {
                phaseManager.startPhase(phase);
            }
            message += "&a at phase &l" + phase.getName();
        } else {
            // Phase is the first in the group
            // So start group with the starting script
            phaseManager.startPhaseGroup(group);
        }
        if (timer != null) {
            phaseManager.setSecondsRemaining(timer);
            message += "&a with timer at &l" + timer + " seconds";
        }
        return CommandResult.success(message + ".");
    }

    private Phase parsePhase(PhaseGroup group, ArgumentHandler argHandler) {
        String[] args = argHandler.getArgs();
        int index = getArguments().indexOf(phaseArgument);
        if (args.length <= index) {
            return null; // No phase specified
        }
        String phaseName = args[index];
        Phase phase = group.getPhases().get(phaseName);
        if (phase == null) {
            throw new CommandException("Phase not found in group."); // Invalid phase
        }
        return phase;
    }

    private static class FakeArgument extends Argument<String> {
        /**
         * Creates a new argument.
         *
         * @param name     the key of the argument
         * @param required whether the argument is required
         */
        public FakeArgument(String name, boolean required) {
            super(name, required);
        }

        @Override
        public List<String> getTabCompletion(CommandSender sender, String arg) {
            return List.of(getName());
        }

        @Override
        public String parse(CommandSender sender, String arg) throws CommandException {
            return getName();
        }
    }
}
