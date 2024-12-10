package me.clickism.clickeventlib.command;

import java.util.ArrayDeque;

/**
 * Represents a stack of subcommands.
 */
public class CommandStack extends ArrayDeque<Subcommand> {
    /**
     * Creates a new command stack.
     */
    public CommandStack() {
        super();
    }

    /**
     * Gets the command path of the stack.
     * <p>
     * i.E: /command subcommand @a -10
     * <p>
     * would return: /command subcommand
     *
     * @return the command path
     */
    public String getPath() {
        StringBuilder builder = new StringBuilder();
        for (Subcommand subcommand : this) {
            builder.insert(0, subcommand.getLabel() + " ");
        }
        builder.insert(0, "/");
        return builder.toString().trim();
    }

    /**
     * Build the usage of the command stack.
     * <p>
     * i.E: /command subcommand [argument]
     *
     * @return the usage of the command stack
     */
    public String buildUsage() {
        Subcommand last = peek();
        if (last == null) return "";
        return getPath() + " " + last.getUsage();
    }
}
