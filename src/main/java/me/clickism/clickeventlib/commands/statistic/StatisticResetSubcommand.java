package me.clickism.clickeventlib.commands.statistic;

import me.clickism.clickeventlib.statistic.Statistic;
import me.clickism.clickeventlib.statistic.StatisticManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.subcommandapi.argument.OfflinePlayersArgument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;

class StatisticResetSubcommand extends Subcommand {
    private static final OfflinePlayersArgument PLAYERS_ARGUMENT = new OfflinePlayersArgument("players", true);

    private final SelectionArgument<Statistic<?>> statisticArgument;

    public StatisticResetSubcommand(StatisticManager statisticManager) {
        super("reset", true);
        this.statisticArgument = new SelectionArgument<>("statistic", true, statisticManager.getStatistics());
        addArgument(statisticArgument);
        addArgument(PLAYERS_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Statistic<?> statistic = argHandler.get(statisticArgument);
        Collection<OfflinePlayer> players = argHandler.get(PLAYERS_ARGUMENT);
        players.forEach(player -> statistic.remove(player.getUniqueId()));
        return CommandResult.success("Removed &l" + statistic.getName() + "&a value for players: &l" + FormatUtils.formatPlayers(players));
    }
}
