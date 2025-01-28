package me.clickism.clickeventlib.commands.statistic;

import me.clickism.clickeventlib.statistic.Statistic;
import me.clickism.clickeventlib.statistic.StatisticManager;
import me.clickism.subcommandapi.argument.OfflinePlayersArgument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;

class StatisticGetSubcommand extends Subcommand {
    private static final OfflinePlayersArgument PLAYERS_ARGUMENT = new OfflinePlayersArgument("players", true);

    private final SelectionArgument<Statistic<?>> statisticArgument;

    public StatisticGetSubcommand(StatisticManager statisticManager) {
        super("get", true);
        this.statisticArgument = new SelectionArgument<>("statistic", true, statisticManager.getStatistics());
        addArgument(statisticArgument);
        addArgument(PLAYERS_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Statistic<?> statistic = argHandler.get(statisticArgument);
        Collection<OfflinePlayer> players = argHandler.get(PLAYERS_ARGUMENT);
        StringBuilder sb = new StringBuilder("Values for &l" + statistic.getName() + ":");
        players.forEach(player -> {
            Object value = statistic.getOrNull(player.getUniqueId());
            if (value == null) return;
            sb.append("\n&a- ").append(player.getName()).append(": &f").append(value);
        });
        return CommandResult.success(sb.toString());
    }
}
