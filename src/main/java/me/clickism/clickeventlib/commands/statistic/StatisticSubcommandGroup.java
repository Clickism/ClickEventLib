package me.clickism.clickeventlib.commands.statistic;

import me.clickism.subcommandapi.command.SubcommandGroup;
import me.clickism.clickeventlib.statistic.StatisticManager;

/**
 * Subcommand group for statistic commands.
 */
public class StatisticSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new statistic subcommand group with the given statistic manager.
     *
     * @param statisticManager the statistic manager
     */
    public StatisticSubcommandGroup(StatisticManager statisticManager) {
        super("statistic", true);
        addSubcommand(new StatisticSetSubcommand(statisticManager));
        addSubcommand(new StatisticGetSubcommand(statisticManager));
        addSubcommand(new StatisticIncrementSubcommand(statisticManager));
        addSubcommand(new StatisticResetSubcommand(statisticManager));
        addSubcommand(new StatisticResetAllSubcommand(statisticManager));
        addSubcommand(new StatisticListSubcommand(statisticManager));
    }
}
