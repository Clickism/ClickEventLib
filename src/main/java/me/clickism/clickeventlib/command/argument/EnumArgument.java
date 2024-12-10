package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an argument that is an enum.
 *
 * @param <E> the type of the enum
 */
public class EnumArgument<E extends Enum<E>> extends Argument<E> {
    private final Class<E> enumClass;

    /**
     * Create a new enum argument with the given name, required status, and enum class.
     *
     * @param name      the name of the argument
     * @param required  whether the argument is required
     * @param enumClass the enum class
     */
    public EnumArgument(String name, boolean required, Class<E> enumClass) {
        super(name, required);
        this.enumClass = enumClass;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> e.name().toLowerCase())
                .toList();
    }

    @Override
    public E parse(CommandSender sender, String arg) throws CommandException {
        try {
            return E.valueOf(enumClass, arg.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new CommandException("Invalid value: &l" + arg + "&c.\n Valid values: &l" +
                    Arrays.stream(enumClass.getEnumConstants())
                            .map(e -> e.name().toLowerCase())
                            .collect(Collectors.joining(", ")));
        }
    }
}
