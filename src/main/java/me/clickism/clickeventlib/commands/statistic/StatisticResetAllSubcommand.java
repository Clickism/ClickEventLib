package me.clickism.clickeventlib.commands.statistic;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.clickeventlib.statistic.Statistic;
import me.clickism.clickeventlib.statistic.StatisticManager;
import org.bukkit.command.CommandSender;

class StatisticResetAllSubcommand extends Subcommand {
    private final SelectionArgument<Statistic<?>> statisticArgument;

    public StatisticResetAllSubcommand(StatisticManager statisticManager) {
        super("reset_all", true);
        this.statisticArgument = new SelectionArgument<>("statistic", true, statisticManager.getStatistics());
        addArgument(statisticArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Statistic<?> statistic = argHandler.get(statisticArgument);
        statistic.clear();
        return CommandResult.success("Removed all values for &l" + statistic.getName() + ".");
    }
}
