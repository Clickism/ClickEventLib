package me.clickism.clickeventlib.commands.leaderboard;

import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import me.clickism.subcommandapi.command.SubcommandGroup;

/**
 * Leaderboard group for leaderboard commands.
 */
public class LeaderboardSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new leaderboard subcommand group with the given leaderboard manager.
     *
     * @param leaderboardManager the leaderboard manager
     */
    public LeaderboardSubcommandGroup(LeaderboardManager leaderboardManager) {
        super("leaderboard", true);
        addSubcommand(new LeaderboardAddSubcommand(leaderboardManager));
        addSubcommand(new LeaderboardTeleportSubcommand(leaderboardManager));
        addSubcommand(new LeaderboardRemoveSubcommand(leaderboardManager));
        addSubcommand(new LeaderboardUpdateSubcommand(leaderboardManager));
    }
}
