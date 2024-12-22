package me.clickism.clickeventlib.commands.statistic;

import me.clickism.clickeventlib.statistic.Statistic;
import me.clickism.clickeventlib.statistic.StatisticManager;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

class StatisticListSubcommand extends Subcommand {
    private final SelectionArgument<Statistic<?>> statisticArgument;

    public StatisticListSubcommand(StatisticManager statisticManager) {
        super("list", true);
        this.statisticArgument = new SelectionArgument<>("statistic", true, statisticManager.getStatistics());
        addArgument(statisticArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Statistic<?> statistic = argHandler.get(statisticArgument);
        StringBuilder sb = new StringBuilder("All entries for &l" + statistic.getName() + ":");
        statistic.getMap().forEach((uuid, value) -> {
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            sb.append("\n&a- ").append(playerName).append(": &f").append(value);
        });
        return CommandResult.success(sb.toString());
    }
}
