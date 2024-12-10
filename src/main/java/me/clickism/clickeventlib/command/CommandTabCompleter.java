package me.clickism.clickeventlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer for commands.
 */
public class CommandTabCompleter implements TabCompleter {
    private final CommandManager commandManager;

    /**
     * Constructor for CommandTabCompleter.
     *
     * @param commandManager command manager to get subcommands from
     */
    public CommandTabCompleter(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, String[] args) {
        if (args.length == 0) return null;
        for (Subcommand subcommand : commandManager.getCommands()) {
            if (!subcommand.getLabel().equalsIgnoreCase(label)) continue;
            int index = args.length - 1;
            return sortCompletion(subcommand.getTabCompletion(index, sender, args), args[index]);
        }
        return null;
    }

    @Nullable
    private static List<String> sortCompletion(@Nullable List<String> completion, String searchText) {
        if (completion == null) return null;
        return completion.stream()
                .filter(str -> isTabCompletionCandidate(str, searchText))
                .sorted()
                .collect(Collectors.toList());
    }

    private static boolean isTabCompletionCandidate(String string, String searchText) {
        return isSubsequence(string.toLowerCase(), searchText.toLowerCase());
    }

    private static boolean isSubsequence(String string, String searchText) {
        int stringIndex = 0;
        int searchTextIndex = 0;
        while (stringIndex < string.length() && searchTextIndex < searchText.length()) {
            if (string.charAt(stringIndex) == searchText.charAt(searchTextIndex)) {
                searchTextIndex++;
            }
            stringIndex++;
        }
        return searchTextIndex == searchText.length();
    }
}
