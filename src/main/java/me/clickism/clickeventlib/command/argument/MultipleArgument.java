package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An argument that takes multiple options.
 *
 * @param <T> the type of options
 */
public abstract class MultipleArgument<T> extends Argument<List<T>> {
    private final Function<T, String> nameFunction;

    /**
     * Creates a new multiple argument.
     *
     * @param name         the name of the argument
     * @param required     whether the argument is required
     * @param nameFunction a function to get the name of an option
     */
    public MultipleArgument(String name, boolean required, Function<T, String> nameFunction) {
        super(name, required);
        this.nameFunction = nameFunction;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        if (arg.startsWith("!")) {
            return getOptions().stream()
                    .map(o -> "!" + nameFunction.apply(o))
                    .toList();
        }
        if (arg.endsWith(",")) {
            return getOptions().stream()
                    .map(o -> arg + nameFunction.apply(o))
                    .toList();
        }
        String[] split = arg.split(",");
        if (split.length > 1) {
            return getSplitTabCompletion(split, arg);
        }
        return getDefaultTabCompletion();
    }

    /**
     * Used for default completion. Returns a modifiable list. Override for custom behavior.
     *
     * @return a list of default completions
     */
    protected List<String> getDefaultTabCompletion() {
        List<String> list = getOptions().stream()
                .map(nameFunction)
                .collect(Collectors.toCollection(ArrayList::new));
        list.add("@a");
        list.add("!");
        return list;
    }

    private List<String> getSplitTabCompletion(String[] split, String arg) {
        String prefix = arg.substring(0, arg.length() - split[split.length - 1].length());
        return getOptions().stream()
                .map(o -> prefix + nameFunction.apply(o))
                .toList();
    }

    @Override
    public List<T> parse(CommandSender sender, String arg) throws CommandException {
        if (arg.equalsIgnoreCase("@a")) {
            return getOptions();
        }
        if (arg.startsWith("!")) {
            return parseFiltered(sender, arg.substring(1));
        }
        return parseSplit(sender, arg);
    }

    /**
     * Gets all options for this argument.
     *
     * @return a list of options
     */
    public abstract List<T> getOptions();

    /**
     * Parses an option from a string.
     * @param sender the sender
     * @param arg the string
     * @return the option
     * @throws CommandException if the option is invalid
     */
    public abstract T parseOption(CommandSender sender, String arg) throws CommandException;

    private List<T> parseFiltered(CommandSender sender, String filterArg) throws CommandException {
        T filter = parseOption(sender, filterArg);
        return getOptions().stream()
                .filter(option -> !option.equals(filter))
                .toList();
    }

    private List<T> parseSplit(CommandSender sender, String arg) throws CommandException {
        String[] split = arg.split(",");
        return Arrays.stream(split)
                .map(option -> parseOption(sender, option))
                .toList();
    }
}
