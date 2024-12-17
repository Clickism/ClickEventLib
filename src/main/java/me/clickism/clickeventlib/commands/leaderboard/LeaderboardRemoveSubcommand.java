package me.clickism.clickeventlib.commands.leaderboard;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.clickeventlib.leaderboard.Leaderboard;
import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import org.bukkit.command.CommandSender;

class LeaderboardRemoveSubcommand extends Subcommand {
    
    private final SelectionArgument<Leaderboard> leaderboardArgument;
    
    private final LeaderboardManager leaderboardManager;
    
    public LeaderboardRemoveSubcommand(LeaderboardManager leaderboardManager) {
        super("remove", true);
        this.leaderboardManager = leaderboardManager;
        this.leaderboardArgument = new SelectionArgument<>("leaderboard", true, leaderboardManager.getLeaderboards());
        addArgument(leaderboardArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Leaderboard leaderboard = argHandler.get(leaderboardArgument);
        leaderboardManager.removeLeaderboard(leaderboard);
        return CommandResult.success("Leaderboard &l" + leaderboard.getName() + "&a removed.");
    }
}
