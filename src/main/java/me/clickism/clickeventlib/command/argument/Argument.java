package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents an argument for a command.
 *
 * @param <T> the type of the argument
 */
public abstract class Argument<T> {
    private static final String HINT_FORMAT = "<%s>";
    private static final String OPTIONAL_HINT_FORMAT = "[%s]";

    private final String name;
    private final String hint;
    private final boolean required;

    /**
     * Creates a new argument.
     *
     * @param name     the key of the argument
     * @param required whether the argument is required
     */
    public Argument(String name, boolean required) {
        this.name = name;
        this.hint = String.format(required ? HINT_FORMAT : OPTIONAL_HINT_FORMAT, name);
        this.required = required;
    }

    /**
     * Gets the tab completion for the argument.
     *
     * @param sender command sender
     * @param arg    argument
     * @return tab completion
     */
    public abstract List<String> getTabCompletion(CommandSender sender, String arg);

    /**
     * Parses the argument from an argument string.
     *
     * @param sender command sender
     * @param arg    argument
     * @return the parsed argument
     * @throws CommandException if the argument is invalid
     */
    public abstract T parse(CommandSender sender, String arg) throws CommandException;

    /**
     * Gets the key of the argument.
     *
     * @return the key of the argument
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the usage hint for the argument.
     *
     * @return the hint for the argument
     */
    public String getHint() {
        return hint;
    }

    /**
     * Whether the argument is required.
     *
     * @return true if the argument is required, false otherwise
     */
    public boolean isRequired() {
        return required;
    }
}
