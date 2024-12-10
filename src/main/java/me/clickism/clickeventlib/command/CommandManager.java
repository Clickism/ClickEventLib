package me.clickism.clickeventlib.command;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.util.Parameterizer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages commands and their execution.
 */
public class CommandManager implements CommandExecutor {
    private static final String USAGE_KEY = "usage";

    private final TabCompleter tabCompleter = new CommandTabCompleter(this);
    private final List<Subcommand> commands = new ArrayList<>();

    /**
     * Creates a new command manager.
     */
    public CommandManager() {
    }

    /**
     * Registers a root command.
     *
     * @param subcommand subcommand to register as a root command
     */
    @AutoRegistered(type = {RegistryType.COMMAND, RegistryType.TAB_COMPLETER})
    public void registerCommand(Subcommand subcommand) {
        commands.add(subcommand);
        PluginCommand pluginCommand = Bukkit.getPluginCommand(subcommand.getLabel());
        if (pluginCommand == null) {
            throw new IllegalArgumentException("This (root) subcommand's label is not registered: '" +
                    subcommand.getLabel() + "' .Check plugin.yml.");
        }
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(tabCompleter);
    }

    /**
     * Get the list of root subcommands.
     *
     * @return list of root subcommands
     */
    public List<Subcommand> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        for (Subcommand subcommand : commands) {
            if (!subcommand.getLabel().equalsIgnoreCase(label)) continue;
            CommandStack trace = new CommandStack();
            try {
                ArgumentHandler argHandler = new ArgumentHandler(sender, subcommand, args);
                trace.push(subcommand);
                CommandResult result = subcommand.executeIfAllowed(trace, sender, argHandler);
                handleCommandResult(trace, sender, result);
            } catch (CommandException exception) {
                handleCommandResult(trace, sender, CommandResult.failure(exception.getMessage()));
            } catch (Exception exception) {
                MessageType.FAIL.send(sender, "An error occurred while executing this command: &l"
                        + exception.getMessage());
                Bukkit.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
            }
            return true;
        }
        return true;
    }

    /**
     * Handles the result of a command execution
     */
    private static void handleCommandResult(CommandStack trace, CommandSender sender, CommandResult result) {
        if (result.getMessage() == null) return;
        switch (result.getType()) {
            case SUCCESS -> MessageType.CONFIRM.send(sender, result.getMessage());
            case FAILURE -> MessageType.FAIL.send(sender, new Parameterizer()
                    .put(USAGE_KEY, trace.buildUsage())
                    .apply(result.getMessage()));
            case WARNING -> MessageType.WARN.send(sender, result.getMessage());
        }
    }
}
