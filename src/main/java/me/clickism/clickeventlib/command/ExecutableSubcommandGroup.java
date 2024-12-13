package me.clickism.clickeventlib.command;

import org.bukkit.command.CommandSender;

/**
 * A subcommand group that executes a subcommand if no arguments are provided.
 */
public class ExecutableSubcommandGroup extends SubcommandGroup {
    private final Subcommand executable;

    /**
     * Creates a new executable subcommand group.
     * @param label the label of the subcommand group.
     * @param requiresOp whether the subcommand requires operator permissions 
     * @param executable the subcommand to execute if no arguments are provided.
     */
    public ExecutableSubcommandGroup(String label, boolean requiresOp, Subcommand executable) {
        super(label, requiresOp);
        this.executable = executable;
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        if (argHandler.size() == 0) {
            return executable.execute(trace, sender, argHandler);
        }
        return super.execute(trace, sender, argHandler);
    }
}
