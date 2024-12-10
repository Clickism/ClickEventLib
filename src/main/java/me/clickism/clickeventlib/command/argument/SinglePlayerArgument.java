package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an argument that is a single player.
 */
public class SinglePlayerArgument extends Argument<Player> {
    /**
     * Create a new single player argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public SinglePlayerArgument(String name, boolean required) {
        super(name, required);
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        List<String> list = Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        list.add("@r");
        list.add("@p");
        list.add("@s");
        return list;
    }

    @Override
    public Player parse(CommandSender sender, String arg) {
        if (arg.equalsIgnoreCase("@p") || arg.equalsIgnoreCase("@s")) {
            if (sender instanceof Player player) {
                return player;
            }
            throw new CommandException("Only players can use this selector.");
        }
        if (arg.equalsIgnoreCase("@r")) {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                throw new CommandException("No players online.");
            }
            Player[] players = Bukkit.getOnlinePlayers().toArray(Player[]::new);
            return players[(int) (Math.random() * players.length)];
        }
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            throw new CommandException("Invalid player: &l" + arg);
        }
        return player;
    }
}
