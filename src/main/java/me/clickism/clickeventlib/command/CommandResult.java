package me.clickism.clickeventlib.command;

import javax.annotation.Nullable;

/**
 * Represents the result of a command execution
 */
public final class CommandResult {

    /**
     * Represents the type of the command result
     */
    public enum CommandResultType {
        /**
         * Represents a failed command execution.
         */
        FAILURE,
        /**
         * Represents a successful command execution.
         */
        SUCCESS,
        /**
         * Represents a command that executed with warnings.
         */
        WARNING
    }

    private final CommandResultType type;
    private final String message;

    private CommandResult(CommandResultType type, String message) {
        this.type = type;
        this.message = message;
    }

    /**
     * Create a success result with the given message.
     *
     * @param message the message to send to the command sender
     * @return success result
     */
    public static CommandResult success(String message) {
        return new CommandResult(CommandResultType.SUCCESS, message);
    }

    /**
     * Create a success result with no message.
     *
     * @return success result
     */
    public static CommandResult success() {
        return new CommandResult(CommandResultType.SUCCESS, null);
    }

    /**
     * Create a failure result with the given message.
     *
     * @param message the message to send to the command sender
     * @return failure result
     */
    public static CommandResult failure(String message) {
        return new CommandResult(CommandResultType.FAILURE, message);
    }

    /**
     * Create a failure result with no message.
     *
     * @return failure result
     */
    public static CommandResult failure() {
        return new CommandResult(CommandResultType.FAILURE, null);
    }

    /**
     * Create a warning result with the given message.
     *
     * @param message the message to send to the command sender
     * @return warning result
     */
    public static CommandResult warning(String message) {
        return new CommandResult(CommandResultType.WARNING, message);
    }

    /**
     * Create a warning result with no message.
     *
     * @return warning result
     */
    public static CommandResult warning() {
        return new CommandResult(CommandResultType.WARNING, null);
    }

    /**
     * Get the type of the command result.
     *
     * @return the type of the command result
     */
    public CommandResultType getType() {
        return type;
    }

    /**
     * Get the message of the command result.
     *
     * @return the message of the command result
     */
    @Nullable
    public String getMessage() {
        return message;
    }
}
