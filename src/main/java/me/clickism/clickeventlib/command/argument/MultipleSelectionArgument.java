package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import me.clickism.clickeventlib.util.Named;
import me.clickism.clickeventlib.util.NamedCollection;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * An argument that takes multiple options.
 *
 * @param <T> the type of options
 */
public class MultipleSelectionArgument<T extends Named> extends MultipleArgument<T> {
    private final NamedCollection<T> options;

    /**
     * Creates a new multiple selection argument.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     * @param options  the options
     */
    public MultipleSelectionArgument(String name, boolean required, NamedCollection<T> options) {
        super(name, required, Named::getName);
        this.options = options;
    }

    @Override
    public List<T> getOptions() {
        return new ArrayList<>(options);
    }

    @Override
    public T parseOption(CommandSender sender, String arg) throws CommandException {
        T t = options.get(arg);
        if (t == null) {
            throw new CommandException("Invalid selection: &l" + arg);
        }
        return t;
    }
}
