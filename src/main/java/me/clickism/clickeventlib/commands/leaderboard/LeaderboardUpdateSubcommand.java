package me.clickism.clickeventlib.commands.leaderboard;

import me.clickism.subcommandapi.command.*;
import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import org.bukkit.command.CommandSender;

class LeaderboardUpdateSubcommand extends Subcommand {
    
    private final LeaderboardManager leaderboardManager;
    
    public LeaderboardUpdateSubcommand(LeaderboardManager leaderboardManager) {
        super("update", true);
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        if (leaderboardManager.getLeaderboards().isEmpty()) {
            return CommandResult.failure("No leaderboards to update.");
        }
        leaderboardManager.updateLeaderboards();
        return CommandResult.success("Leaderboards updated.");
    }
}
