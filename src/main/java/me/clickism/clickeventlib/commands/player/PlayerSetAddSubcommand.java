package me.clickism.clickeventlib.commands.player;

import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.clickeventlib.util.PlayerSet;
import me.clickism.subcommandapi.argument.PlayersArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

class PlayerSetAddSubcommand extends Subcommand {
    private static final PlayersArgument PLAYERS_ARGUMENT = new PlayersArgument("players", true);
    private final PlayerSet playerSet;

    public PlayerSetAddSubcommand(PlayerSet playerSet) {
        super("add", true);
        this.playerSet = playerSet;
        addArgument(PLAYERS_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack commandStack, CommandSender commandSender, ArgumentHandler argumentHandler) throws CommandException {
        Collection<Player> players = argumentHandler.get(PLAYERS_ARGUMENT);
        players.forEach(playerSet::add);
        return CommandResult.success("Added players to set: " + FormatUtils.formatPlayers(players));
    }
}
