package me.clickism.clickeventlib.commands.team;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.OfflinePlayersArgument;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.team.EventTeam;
import me.clickism.clickeventlib.team.TeamManager;
import me.clickism.clickeventlib.util.FormatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Subcommand for joining teams.
 * <p>Usage: /join &lt;team&gt; [players]</p>
 */
public class JoinSubcommand extends Subcommand {

    private final SelectionArgument<EventTeam> teamArgument;
    private final OfflinePlayersArgument playersArgument = new OfflinePlayersArgument("players", false);

    private final TeamManager teamManager;
    private final ChatManager chatManager;

    /**
     * Creates a new team subcommand.
     *
     * @param teamManager the team manager
     * @param chatManager the chat manager
     * @param requiresOp  whether the command requires operator permissions
     */
    public JoinSubcommand(TeamManager teamManager, ChatManager chatManager, boolean requiresOp) {
        super("join", requiresOp);
        this.teamManager = teamManager;
        this.chatManager = chatManager;
        this.teamArgument = new SelectionArgument<>("team", true, teamManager.getTeams());
        addArgument(teamArgument);
        addArgument(playersArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        EventTeam team = argHandler.get(teamArgument);
        List<OfflinePlayer> players = argHandler.getOrNull(playersArgument);
        if (players != null) {
            // Operator command
            if (!sender.isOp()) return CommandResult.failure("You can't use this command");
            players.forEach(player -> {
                teamManager.joinTeam(player, team);
                chatManager.refreshName(player);
            });
            return CommandResult.success("Added player(s): &l" + FormatUtils.formatPlayers(players) + " &ato team &l" + team.getName() + "&a.");
        }
        // Public command
        if (!(sender instanceof Player player)) {
            return CommandResult.failure("You must be a player to add yourself to a team.");
        }
        return switch (team.getJoinSetting(teamManager.getDefaultJoinSetting())) {
            case EVERYONE_OPEN -> {
                teamManager.joinTeam(player, team);
                chatManager.refreshName(player);
                yield CommandResult.success("Joined team &l" + team.getName() + "&a.");
            }
            case EVERYONE_INVITE -> {
                if (!teamManager.isInvited(player, team)) {
                    yield CommandResult.failure("You haven't been invited to this team.");
                }
                teamManager.joinTeam(player, team);
                chatManager.refreshName(player);
                yield CommandResult.success("Joined team &l" + team.getName() + "&a.");
            }
            case OPERATOR -> {
                if (!player.isOp()) {
                    yield CommandResult.failure("You can't join this team.");
                }
                teamManager.joinTeam(player, team);
                chatManager.refreshName(player);
                yield CommandResult.success("Joined team &l" + team.getName() + "&a.");
            }
            case USE_DEFAULT -> throw new IllegalStateException("Unexpected USE_DEFAULT join setting.");
        };
    }
}
