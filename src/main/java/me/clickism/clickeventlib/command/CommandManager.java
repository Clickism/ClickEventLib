package me.clickism.clickeventlib.command;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.util.NamedCollection;
import me.clickism.clickeventlib.util.Parameterizer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Manages commands and their execution.
 */
public class CommandManager implements CommandExecutor {
    private static final String USAGE_KEY = "usage";

    private final TabCompleter tabCompleter = new CommandTabCompleter(this);
    private final NamedCollection<Subcommand> commands = new NamedCollection<>(new ArrayList<>());

    /**
     * Creates a new command manager.
     */
    public CommandManager() {
    }
    
    /**
     * Registers a root command.
     * <p>If the root command is not registered in plugin.yml, will do nothing.</p>
     *
     * @param subcommand subcommand to register as a root command
     */
    @AutoRegistered(type = {RegistryType.COMMAND, RegistryType.TAB_COMPLETER})
    public void registerCommand(Subcommand subcommand) {
        commands.add(subcommand);
        PluginCommand pluginCommand = Bukkit.getPluginCommand(subcommand.getLabel());
        if (pluginCommand == null) {
            Bukkit.getLogger().log(Level.SEVERE, "This (root) subcommand's label is not registered: '" +
                    subcommand.getLabel() + "'. Check plugin.yml.");
            return;
        }
        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(tabCompleter);
    }

    /**
     * Get the named collection of root subcommands.
     *
     * @return named collection of root subcommands
     */
    public NamedCollection<Subcommand> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                             @NotNull String label, String[] args) {
        Subcommand subcommand = commands.get(label);
        if (subcommand == null) return false;
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
            String commandString = command.getLabel() + " " + String.join(" ", args);
            Bukkit.getLogger().log(Level.SEVERE, 
                    "An error occurred when " + sender.getName() + " tried executing command '/" + commandString +
                    "': " + exception.getMessage(), exception);
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
