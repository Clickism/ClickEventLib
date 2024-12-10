package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import me.clickism.clickeventlib.util.Named;
import me.clickism.clickeventlib.util.NamedCollection;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents an argument that is selected from a collection of named options.
 * The argument is parsed by selecting the option with the given name using the NamedCollection.
 *
 * @param <T> type of the named options
 */
public class SelectionArgument<T extends Named> extends Argument<T> {
    private final NamedCollection<T> options;

    /**
     * Creates a new selection argument.
     *
     * @param name     name of the argument
     * @param required whether the argument is required
     * @param options  collection of named options
     */
    public SelectionArgument(String name, boolean required, NamedCollection<T> options) {
        super(name, required);
        this.options = options;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        return options.stream()
                .map(Named::getName)
                .toList();
    }

    @Override
    public T parse(CommandSender sender, String arg) throws CommandException {
        T option = options.get(arg);
        if (option == null) {
            throw new CommandException("Invalid " + getName() + ": &l" + arg);
        }
        return option;
    }
}
