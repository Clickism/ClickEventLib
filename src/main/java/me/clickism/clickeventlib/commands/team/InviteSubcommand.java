package me.clickism.clickeventlib.commands.team;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.team.EventTeam;
import me.clickism.subcommandapi.argument.SinglePlayerArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.entity.Player;

/**
 * Subcommand to invite a player to a team.
 * <p>Usage: /invite &lt;player&gt;</p>
 */
public class InviteSubcommand extends PlayerOnlySubcommand {
    private static final SinglePlayerArgument PLAYER_ARGUMENT = new SinglePlayerArgument("player", true);

    /**
     * Create a new invite subcommand.
     *
     * @param requiresOp true if the command requires operator permissions, false otherwise
     */
    public InviteSubcommand(boolean requiresOp) {
        super("invite", requiresOp);
        addArgument(PLAYER_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player sender, ArgumentHandler argHandler) throws CommandException {
        Player player = argHandler.get(PLAYER_ARGUMENT);
        EventTeam senderTeam = EventTeam.getTeamOf(sender);
        EventTeam playerTeam = EventTeam.getTeamOf(player);
        if (senderTeam == null) {
            return CommandResult.failure("You are not in a team. Use &4&l/join &cto join a team first.");
        }
        if (senderTeam.equals(playerTeam)) {
            return CommandResult.failure("This player is already in your team.");
        }
        if (senderTeam.isInvited(player)) {
            return CommandResult.failure("You have already invited this player.");
        }
        return switch (senderTeam.getJoinSetting()) {
            case EVERYONE_OPEN -> CommandResult.warning("This team is open to everyone.");
            case EVERYONE_INVITE -> {
                if (!senderTeam.invite(player)) {
                    yield CommandResult.failure("This player is already invited to your team.");
                }
                MessageType.WARN.send(player, "You have been invited to join " + senderTeam.getColor() + "&l" + senderTeam.getName() +
                        "&e by &l" + sender.getName() + "&e. Do &l/join " + senderTeam.getName() + " &eto accept.");
                yield CommandResult.success("Invited &l" + player.getName() + " &ato your team.");
            }
            case OPERATOR -> {
                if (!sender.isOp()) {
                    yield CommandResult.failure("You can't invite players to this team.");
                }
                yield CommandResult.failure("You can't invite players to this team. Use &4&l/join <team> <players> &cto add players.");
            }
        };
    }
}
