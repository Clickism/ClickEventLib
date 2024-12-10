package me.clickism.clickeventlib.command;

import me.clickism.clickeventlib.command.argument.Argument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A class that handles the arguments passed to a command.
 */
public class ArgumentHandler {
    private final CommandSender sender;
    private final String[] args;
    private final Map<Argument<?>, Object> parsedArguments = new HashMap<>();
    private final Set<String> flags;

    /**
     * Creates a new ArgumentHandler with the given arguments.
     *
     * @param sender     command sender
     * @param subcommand subcommand that will use the arguments
     * @param args       arguments
     */
    public ArgumentHandler(CommandSender sender, Subcommand subcommand, String[] args) throws CommandException {
        this.sender = sender;
        this.args = args;
        this.flags = new HashSet<>(subcommand.getFlags().size());
        parseFlags(subcommand);
        parseArguments(subcommand);
    }

    /**
     * Returns a new ArgumentHandler with trimmed arguments that will be used by the given subcommand.
     *
     * @param subcommand subcommand that will use the new arguments
     * @return new ArgumentHandler with trimmed arguments
     */
    public ArgumentHandler trimmed(Subcommand subcommand) throws CommandException {
        return new ArgumentHandler(sender, subcommand, trimArgs(args));
    }

    /**
     * Get all arguments.
     *
     * @return arguments
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Parses the flags for the given subcommand.
     */
    private void parseFlags(Subcommand subcommand) throws CommandException {
        List<String> subcommandFlags = subcommand.getFlags();
        // Parse flags
        if (!subcommandFlags.isEmpty()) {
            for (String arg : args) {
                if (!arg.startsWith("--")) continue;
                if (arg.length() < 3 || !subcommandFlags.contains(arg.substring(2).toLowerCase())) {
                    throw new CommandException("Invalid flag: " + arg);
                }
                flags.add(arg.substring(2).toLowerCase());
            }
        }
    }

    /**
     * Parses the arguments for the given subcommand.
     */
    private void parseArguments(Subcommand subcommand) throws CommandException {
        List<Argument<?>> arguments = subcommand.getArguments();
        // Parse arguments
        for (int i = 0; i < arguments.size(); i++) {
            Argument<?> argument = arguments.get(i);
            if (args.length <= i) {
                if (!argument.isRequired()) break;
                throw new CommandException("Not enough arguments. Usage: {usage}");
            }
            String arg = args[i];
            if (arg.startsWith("--")) {
                continue; // Skip flag
            }
            parsedArguments.put(argument, argument.parse(sender, arg));
        }
    }

    /**
     * Returns the number of arguments.
     *
     * @return number of arguments
     */
    public int size() {
        return args.length;
    }

    /**
     * Get the passed value for the given argument.
     *
     * @param argument argument
     * @param <T>      type of the parsed argument
     * @return value of the argument
     * @throws CommandException if there was no value passed for this argument
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T get(Argument<T> argument) {
        if (!parsedArguments.containsKey(argument)) {
            throw new CommandException("Expected argument: " + argument.getName());
        }
        return (T) parsedArguments.get(argument);
    }

    /**
     * Get the passed value for the given argument.
     *
     * @param argument argument
     * @param <T>      type of the parsed argument
     * @return value of the argument, or null if no value was passed
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getOrNull(Argument<T> argument) {
        return (T) parsedArguments.get(argument);
    }

    /**
     * Get the passed value for the given argument, or the default value if no value was passed.
     *
     * @param argument     argument
     * @param defaultValue default value
     * @param <T>          type of the parsed argument
     * @return value of the argument, or the default value if no value was passed
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(Argument<T> argument, T defaultValue) {
        T value = (T) parsedArguments.get(argument);
        return value != null ? value : defaultValue;
    }

    /**
     * Returns whether a value was passed for the given argument.
     *
     * @param argument argument
     * @return true if a value was passed for the argument
     */
    public boolean has(Argument<?> argument) {
        return parsedArguments.containsKey(argument);
    }

    /**
     * Returns whether the given flag was passed.
     *
     * @param flag flag
     * @return true if the flag was passed
     */
    public boolean hasFlag(String flag) {
        return flags.contains(flag.toLowerCase());
    }

    /**
     * Trim the arguments array (shift it to the left).
     *
     * @param args arguments
     * @return trimmed arguments
     */
    public static String[] trimArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }
}