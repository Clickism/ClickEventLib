package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.phase.Phase;
import me.clickism.clickeventlib.phase.PhaseManager;
import me.clickism.subcommandapi.argument.IntegerArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;

class PhaseSkipSubcommand extends Subcommand {
    private static final IntegerArgument SKIP_AMOUNT = new IntegerArgument("amount", false);

    private final PhaseManager phaseManager;

    public PhaseSkipSubcommand(PhaseManager phaseManager) {
        super("skip", true);
        this.phaseManager = phaseManager;
        addArgument(SKIP_AMOUNT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        int skipAmount = argHandler.getOrDefault(SKIP_AMOUNT, 1);
        int skipped = 0;
        Phase phase = null;
        while (skipAmount-- > 0) {
            Phase newPhase = phaseManager.startNextPhase();
            skipped++;
            if (newPhase == null) {
                break;
            }
            phase = newPhase;
        }
        if (phase == null) {
            return CommandResult.failure("No phases to skip to.");
        }
        String phaseName = phase.getName();
        return switch (skipped) {
            case 0 -> CommandResult.failure("No phases to skip to.");
            case 1 -> CommandResult.success("Skipped to the next phase: &l" + phaseName);
            default -> CommandResult.success("Skipped to the phase: &l" + phaseName + " &2(" + skipped + " phases skipped)");
        };
    }
}
