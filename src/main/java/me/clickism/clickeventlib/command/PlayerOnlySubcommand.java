package me.clickism.clickeventlib.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents a subcommand that can only be executed by a player.
 */
public abstract class PlayerOnlySubcommand extends Subcommand {

    /**
     * Create a new subcommand with the given label that can only be executed by a player.
     *
     * @param label      the label of the subcommand
     * @param requiresOp whether the subcommand requires operator permissions
     */
    public PlayerOnlySubcommand(String label, boolean requiresOp) {
        super(label, requiresOp);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        if (!(sender instanceof Player player)) {
            return CommandResult.failure("Only players can use this command.");
        }
        return execute(trace, player, argHandler);
    }

    /**
     * Execute the subcommand with the given player and argument handler.
     *
     * @param trace      the command stack trace
     * @param player     the player executing the command
     * @param argHandler the argument handler
     * @return the result of the command
     */
    protected abstract CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException;
}
