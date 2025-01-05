package me.clickism.clickeventlib.util;

import me.clickism.clickeventlib.trigger.TriggerBox;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Utility class for formatting strings.
 */
public class FormatUtils {
    /**
     * No constructor for static class.
     */
    private FormatUtils() {
    }

    /**
     * Title cases a string. (i.E.: "HELLO world" -> "Hello World")
     *
     * @param string the string to title case
     * @return the title cased string
     */
    public static String titleCase(String string) {
        return capitalize(string.toLowerCase());
    }

    /**
     * Formats a namespace into a human-readable string. (i.E: "my_namespace" -> "My Namespace")
     *
     * @param namespace namespace to format
     * @return the formatted namespace
     */
    public static String formatNamespace(String namespace) {
        return titleCase(namespace.replace("_", " "));
    }

    /**
     * Capitalizes the first letter of each word in a string.
     *
     * @param string the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String string) {
        String[] words = string.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            capitalizedString.append(capitalizeWord(word)).append(" ");
        }
        return capitalizedString.toString().trim();
    }

    private static String capitalizeWord(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Format a collection of trigger boxes into a string.
     *
     * @param list the collection of trigger boxes
     * @return the formatted string
     */
    public static String formatTriggerBoxes(Collection<TriggerBox> list) {
        StringBuilder sb = new StringBuilder();
        for (TriggerBox triggerBox : list) {
            sb.append("\n&a- ").append(formatTriggerBox(triggerBox));
        }
        return sb.toString();
    }

    /**
     * Format a trigger box into a string.
     *
     * @param triggerBox the trigger box
     * @return the formatted string
     */
    public static String formatTriggerBox(TriggerBox triggerBox) {
        return "&f&l" + triggerBox.getName() + " &6/ &bZ: &l" + triggerBox.getZ();
    }

    /**
     * Formats a number of milliseconds into a human-readable time format.
     *
     * @param millis the number of milliseconds
     * @return the formatted time
     */
    public static String formatMillis(long millis) {
        String milliPart = String.format(".%01d", millis % 1000 / 100);
        return formatSeconds(millis / 1000) + milliPart;
    }

    /**
     * Formats a number of seconds into a human-readable time format.
     *
     * @param seconds the number of seconds
     * @return the formatted time
     */
    public static String formatSeconds(long seconds) {
        if (seconds < 0) {
            return "∞";
        }
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        if (days > 0) {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        }
        return String.format("%d", seconds);
    }

    /**
     * Formats a collection of players into a human-readable list.
     *
     * @param players the players to format
     * @return the formatted list
     */
    public static String formatPlayers(Collection<? extends OfflinePlayer> players) {
        if (players.isEmpty()) {
            return "&c&lNobody";
        }
        if (players.size() > 1 && players.size() == Bukkit.getOnlinePlayers().size()) {
            return "&e&lEveryone";
        }
        return players.stream()
                .map(OfflinePlayer::getName)
                .collect(Collectors.joining(", "));
    }
}
