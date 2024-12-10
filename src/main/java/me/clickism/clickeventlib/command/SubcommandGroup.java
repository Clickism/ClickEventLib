package me.clickism.clickeventlib.command;

import me.clickism.clickeventlib.command.argument.SubcommandArgument;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * A subcommand group is a subcommand that contains multiple subcommands.
 */
public class SubcommandGroup extends Subcommand {
    private final List<Subcommand> subcommands = new ArrayList<>();
    private final SubcommandArgument subcommandArgument;

    /**
     * Create a new subcommand group with the given label and subcommands.
     *
     * @param label      the label of the subcommand group
     * @param requiresOp whether the subcommand requires operator permissions
     */
    public SubcommandGroup(String label, boolean requiresOp) {
        super(label, requiresOp);
        this.subcommandArgument = new SubcommandArgument(this.subcommands, false);
        addArgument(subcommandArgument);
    }

    /**
     * Add a subcommand to the group.
     *
     * @param subcommand the subcommand to add
     * @return the subcommand group
     */
    public SubcommandGroup addSubcommand(Subcommand subcommand) {
        subcommands.add(subcommand);
        return this;
    }

    @Override
    public String getUsage() {
        return subcommandArgument.getHint();
    }

    @Override
    public List<String> getTabCompletion(int index, CommandSender sender, String[] args) {
        if (index == 0) {
            // Return the subcommands of the group that can be performed by the sender
            return subcommandArgument.getTabCompletion(sender, args[index]);
        }
        for (Subcommand subcommand : subcommands) {
            if (!subcommand.getLabel().equalsIgnoreCase(args[0])) continue;
            // Return the tab completion for the subcommand
            return subcommand.getTabCompletion(index - 1, sender, ArgumentHandler.trimArgs(args));
        }
        return null;
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Subcommand subcommand = argHandler.getOrNull(subcommandArgument);
        if (subcommand == null) {
            return CommandResult.failure("Please provide a subcommand: &l" + getUsage());
        }
        trace.push(subcommand);
        return subcommand.executeIfAllowed(trace, sender, argHandler.trimmed(subcommand));
    }
}
