package me.clickism.clickeventlib.commands.role;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.clickeventlib.team.RoleManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.subcommandapi.argument.PlayersArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

class RoleRemoveSubcommand extends Subcommand {
    private static final PlayersArgument PLAYERS_ARGUMENT = new PlayersArgument("players", true);

    private final RoleManager roleManager;
    private final ChatManager chatManager;

    public RoleRemoveSubcommand(RoleManager roleManager, ChatManager chatManager) {
        super("remove", true);
        this.roleManager = roleManager;
        this.chatManager = chatManager;
        addArgument(PLAYERS_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        List<Player> players = argHandler.get(PLAYERS_ARGUMENT);
        players.forEach(player -> {
            roleManager.removeRole(player.getUniqueId());
            chatManager.refreshName(player);
        });
        roleManager.save();
        return CommandResult.success("Removed roles from player(s): &l" + FormatUtils.formatPlayers(players));
    }
}
