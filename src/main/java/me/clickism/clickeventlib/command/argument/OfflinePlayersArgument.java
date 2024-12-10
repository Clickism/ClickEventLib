package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An argument that takes multiple offline players.
 */
public class OfflinePlayersArgument extends MultipleArgument<OfflinePlayer> {

    /**
     * Creates a new multiple offline players argument.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public OfflinePlayersArgument(String name, boolean required) {
        super(name, required, OfflinePlayer::getName);
    }
    
    @Override
    protected List<String> getDefaultTabCompletion() {
        List<String> list = super.getDefaultTabCompletion();
        list.add("@p");
        list.add("@s");
        return list;
    }

    @Override
    public List<OfflinePlayer> getOptions() {
        return new ArrayList<>(Arrays.asList(Bukkit.getOfflinePlayers()));
    }

    @Override
    public OfflinePlayer parseOption(CommandSender sender, String arg) throws CommandException {
        if (arg.equalsIgnoreCase("@p") || arg.equalsIgnoreCase("@s")) {
            if (!(sender instanceof OfflinePlayer player)) {
                throw new CommandException("Only players can use this selector.");
            }
            return player;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        if (!player.hasPlayedBefore()) {
            throw new CommandException("This player has not played before: &l" + arg);
        }
        return player;
    }
}
