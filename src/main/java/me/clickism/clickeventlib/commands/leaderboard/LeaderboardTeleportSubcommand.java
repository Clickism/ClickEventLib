package me.clickism.clickeventlib.commands.leaderboard;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.clickeventlib.leaderboard.Leaderboard;
import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class LeaderboardTeleportSubcommand extends PlayerOnlySubcommand {
    
    private final SelectionArgument<Leaderboard> leaderboardArgument;
    
    public LeaderboardTeleportSubcommand(LeaderboardManager leaderboardManager) {
        super("teleport", true);
        this.leaderboardArgument = new SelectionArgument<>("leaderboard", true, leaderboardManager.getLeaderboards());
        addArgument(leaderboardArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Leaderboard leaderboard = argHandler.get(leaderboardArgument);
        Location location = leaderboard.getSafeLocation().getLocation();
        String leaderboardName = leaderboard.getName();
        if (location == null) {
            return CommandResult.failure("Location's world not loaded for &l" + leaderboardName + "&c.");
        }
        player.teleport(location);
        return CommandResult.success("Teleported to leaderboard &l" + leaderboardName + "&a.");
    }
}
