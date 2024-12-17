package me.clickism.clickeventlib.commands.statistic;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.OfflinePlayersArgument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.argument.StringArgument;
import me.clickism.clickeventlib.statistic.Statistic;
import me.clickism.clickeventlib.statistic.StatisticManager;
import me.clickism.clickeventlib.util.FormatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;

class StatisticSetSubcommand extends Subcommand {
    private static final OfflinePlayersArgument PLAYERS_ARGUMENT = new OfflinePlayersArgument("players", true);
    private static final StringArgument VALUE_ARGUMENT = new StringArgument("value", true);

    private final SelectionArgument<Statistic<?>> statisticArgument;

    public StatisticSetSubcommand(StatisticManager statisticManager) {
        super("set", true);
        this.statisticArgument = new SelectionArgument<>("statistic", true, statisticManager.getStatistics());
        addArgument(statisticArgument);
        addArgument(PLAYERS_ARGUMENT);
        addArgument(VALUE_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Statistic<?> statistic = argHandler.get(statisticArgument);
        Collection<OfflinePlayer> players = argHandler.get(PLAYERS_ARGUMENT);
        String value = argHandler.get(VALUE_ARGUMENT);
        try {
            players.forEach(player -> statistic.parseAndSet(player.getUniqueId(), value));
        } catch (Exception exception) {
            throw new CommandException(exception.getMessage());
        }
        return CommandResult.success("Set statistic &l" + statistic.getName() + "&a to &f&l" + value +
                "&a for players: &l" + FormatUtils.formatPlayers(players));
    }
}
