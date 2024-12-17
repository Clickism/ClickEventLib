package me.clickism.clickeventlib.commands.leaderboard;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.*;
import me.clickism.clickeventlib.leaderboard.Leaderboard;
import me.clickism.clickeventlib.leaderboard.LeaderboardEntryProvider;
import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import me.clickism.clickeventlib.location.SafeLocation;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class LeaderboardAddSubcommand extends PlayerOnlySubcommand {
    private static final TextArgument TITLE_ARGUMENT = new TextArgument("title", true);
    private static final EnumArgument<ChatColor> COLOR_ARGUMENT = new EnumArgument<>("color", true, ChatColor.class);
    private static final DoubleArgument SCALE_ARGUMENT = new DoubleArgument("scale", false);
    private static final IntegerArgument ENTRY_COUNT_ARGUMENT = new IntegerArgument("entry_count", false);
    
    private static final IntegerArgument YAW_ARGUMENT = new IntegerArgument("yaw", false);
    private static final IntegerArgument PITCH_ARGUMENT = new IntegerArgument("pitch", false);
    
    private final SelectionArgument<LeaderboardEntryProvider> providerArgument;
    
    private final LeaderboardManager leaderboardManager;
    
    // ./lobby leaderboard add <provider> <title> <color> [scale] [entryCount]
    public LeaderboardAddSubcommand(LeaderboardManager leaderboardManager) {
        super("add", true);
        this.leaderboardManager = leaderboardManager;
        this.providerArgument = new SelectionArgument<>("provider", true, leaderboardManager.getProviders());
        addArgument(providerArgument);
        addArgument(TITLE_ARGUMENT);
        addArgument(COLOR_ARGUMENT);
        addArgument(SCALE_ARGUMENT);
        addArgument(ENTRY_COUNT_ARGUMENT);
        addArgument(YAW_ARGUMENT);
        addArgument(PITCH_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        LeaderboardEntryProvider provider = argHandler.get(providerArgument);
        String title = Utils.colorize(argHandler.get(TITLE_ARGUMENT));
        ChatColor color = argHandler.get(COLOR_ARGUMENT);
        double scale = argHandler.getOrDefault(SCALE_ARGUMENT, (double) Leaderboard.DEFAULT_SCALE);
        int entryCount = argHandler.getOrDefault(ENTRY_COUNT_ARGUMENT, Leaderboard.DEFAULT_ENTRY_COUNT);
        int id = leaderboardManager.getNextId(provider);
        Location location = player.getLocation();
        int yaw = argHandler.getOrDefault(YAW_ARGUMENT, (int) location.getYaw());
        int pitch = argHandler.getOrDefault(PITCH_ARGUMENT, 0);
        location.setYaw(yaw);
        location.setPitch(pitch);
        Leaderboard leaderboard = new Leaderboard(id, new SafeLocation(location), provider, title, color, 
                entryCount, (float) scale);
        leaderboardManager.addLeaderboard(leaderboard);
        leaderboard.update();
        return CommandResult.success("Leaderboard &l" + leaderboard.getName() + "&a added.");
    }
}
