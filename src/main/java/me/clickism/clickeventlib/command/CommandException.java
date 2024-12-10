package me.clickism.clickeventlib.command;

/**
 * Exception thrown when a command execution fails.
 */
public class CommandException extends IllegalArgumentException {
    /**
     * Create a new command exception with the given message.
     *
     * @param message the message
     */
    public CommandException(String message) {
        super(message);
    }

    /**
     * Create a new command exception with the given message and cause.
     *
     * @param throwable the cause
     */
    public CommandException(Throwable throwable) {
        super(throwable);
    }
}
