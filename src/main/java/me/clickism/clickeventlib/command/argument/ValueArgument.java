package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Function;

/**
 * Represents an argument that has a simple value.
 *
 * @param <T> the type of the value
 */
public abstract class ValueArgument<T> extends Argument<T> {
    private final List<String> possibleValues;
    private final Function<String, T> parser;

    /**
     * Create a new value argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     * @param parser   the parser for the argument
     */
    public ValueArgument(String name, boolean required, Function<String, T> parser) {
        this(name, required, parser, null);
    }

    /**
     * Create a new value argument with the given name, required status, and possible values.
     * All values outside the possible values will be considered invalid.
     *
     * @param name           the name of the argument
     * @param required       whether the argument is required
     * @param parser         the parser for the argument
     * @param possibleValues the possible values of the argument
     */
    public ValueArgument(String name, boolean required, Function<String, T> parser, List<String> possibleValues) {
        super(name, required);
        this.parser = parser;
        this.possibleValues = possibleValues;
    }

    /**
     * Gets the parser for the argument.
     *
     * @return the parser for the argument
     */
    public Function<String, T> getParser() {
        return parser;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        return possibleValues == null ? List.of(getHint()) : possibleValues;
    }

    @Override
    public T parse(CommandSender sender, String arg) throws CommandException {
        if (possibleValues != null && !possibleValues.contains(arg.toLowerCase())) {
            throw new CommandException("Invalid value: &l" + arg);
        }
        try {
            return parser.apply(arg);
        } catch (Exception exception) {
            throw new CommandException("Invalid value: &l" + arg);
        }
    }
}
