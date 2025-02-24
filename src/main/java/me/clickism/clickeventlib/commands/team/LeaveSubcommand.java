package me.clickism.clickeventlib.commands.team;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.clickeventlib.team.EventTeam;
import me.clickism.clickeventlib.team.TeamManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.subcommandapi.argument.OfflinePlayersArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Subcommand group for joining teams.
 */
public class LeaveSubcommand extends Subcommand {

    private final OfflinePlayersArgument playersArgument = new OfflinePlayersArgument("players", false);

    private final ChatManager chatManager;

    /**
     * Creates a new team subcommand.
     *
     * @param chatManager the chat manager
     * @param requiresOp  whether the command requires operator permissions
     */
    public LeaveSubcommand(ChatManager chatManager, boolean requiresOp) {
        super("leave", requiresOp);
        this.chatManager = chatManager;
        addArgument(playersArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        List<OfflinePlayer> players = argHandler.getOrNull(playersArgument);
        if (players == null) {
            if (!(sender instanceof Player player)) {
                return CommandResult.failure("You must be a player to leave a team.");
            }
            EventTeam team = EventTeam.getTeamOf(player);
            if (team == null) {
                return CommandResult.failure("You are not in a team.");
            }
            chatManager.refreshName(player);
            return CommandResult.warning("Left team &l" + team.getName() + ".");
        }
        if (!sender.isOp()) return CommandResult.warning("You can't use this command");
        players.forEach(player -> {
            if (player.getName() == null) return;
            TeamManager.INSTANCE.leaveTeam(player);
            chatManager.refreshName(player);
        });
        return CommandResult.success("Removed players: &l" + FormatUtils.formatPlayers(players) + " &afrom their teams.");
    }
}
