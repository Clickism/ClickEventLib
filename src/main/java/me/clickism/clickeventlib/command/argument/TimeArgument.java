package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents an argument that represents a time value.
 * <p>
 * The parsed value is in seconds.
 */
public class TimeArgument extends Argument<Long> {
    private static final List<Character> TIME_FORMATS = List.of('s', 'm', 'h', 'd');

    /**
     * Create a new time argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public TimeArgument(String name, boolean required) {
        super(name, required);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        if (!arg.isEmpty() && TIME_FORMATS.contains(arg.charAt(arg.length() - 1))) {
            return List.of(arg);
        }
        return TIME_FORMATS.stream()
                .map(c -> arg + c)
                .toList();
    }

    @Override
    public Long parse(CommandSender sender, String arg) throws CommandException {
        try {
            long time = Long.parseLong(arg.substring(0, arg.length() - 1));
            char format = arg.charAt(arg.length() - 1);
            return switch (format) {
                case 's' -> time;
                case 'm' -> time * 60;
                case 'h' -> time * 60 * 60;
                case 'd' -> time * 60 * 60 * 24;
                default -> throw new IllegalArgumentException("Unexpected value: " + format);
            };
        } catch (IllegalArgumentException exception) {
            throw new CommandException("Invalid time format: &l" + arg + "&c.\nExpected: &l<number><s/m/h/d>&c Example: &l10m, 60s, 1d.");
        }
    }
}
