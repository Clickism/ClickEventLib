package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * An argument that takes multiple online players.
 */
public class PlayersArgument extends MultipleArgument<Player> {

    /**
     * Creates a new multiple players argument.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public PlayersArgument(String name, boolean required) {
        super(name, required, Player::getName);
    }

    @Override
    protected List<String> getDefaultTabCompletion() {
        List<String> list = super.getDefaultTabCompletion();
        list.add("@r");
        list.add("@r<count>");
        list.add("@p");
        list.add("@s");
        return list;
    }

    @Override
    public List<Player> parse(CommandSender sender, String arg) throws CommandException {
        if (arg.equalsIgnoreCase("@p") || arg.equalsIgnoreCase("@s")) {
            if (sender instanceof Player player) {
                return List.of(player);
            }
            throw new CommandException("Only players can use this selector.");
        }
        if (arg.startsWith("@r")) {
            Integer count = 1;
            if (arg.length() > 2) {
                count = Utils.parseIntOrNull(arg.substring(2));
            }
            if (count == null) {
                throw new CommandException("Invalid random player selector: &l" + arg +
                        ". Expected: &l@r<count>.&c i.E: &l@r5");
            }
            return selectRandomPlayers(count);
        }
        return super.parse(sender, arg);
    }

    @Override
    public List<Player> getOptions() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public Player parseOption(CommandSender sender, String arg) throws CommandException {
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            throw new CommandException("Invalid player: &l" + arg);
        }
        return player;
    }

    private static List<Player> selectRandomPlayers(int count) {
        if (Bukkit.getOnlinePlayers().size() < count) {
            throw new CommandException("Less than " + count + " players online.");
        }
        List<Player> players = new ArrayList<>(count);
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < count; i++) {
            Player player = onlinePlayers.get((int) (Math.random() * onlinePlayers.size()));
            players.add(player);
            onlinePlayers.remove(player);
        }
        return players;
    }
}
