package me.clickism.clickeventlib.commands.role;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.PlayersArgument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.clickeventlib.team.Role;
import me.clickism.clickeventlib.team.RoleManager;
import me.clickism.clickeventlib.util.FormatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

class RoleSetSubcommand extends Subcommand {
    private static final PlayersArgument PLAYERS_ARGUMENT = new PlayersArgument("players", true);
    private final SelectionArgument<Role> roleArgument;
    
    private final RoleManager roleManager;
    private final ChatManager chatManager;
    
    public RoleSetSubcommand(RoleManager roleManager, ChatManager chatManager) {
        super("set", true);
        this.roleManager = roleManager;
        this.chatManager = chatManager;
        this.roleArgument = new SelectionArgument<>("role", true, roleManager.getRoles());
        addArgument(roleArgument);
        addArgument(PLAYERS_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Role role = argHandler.get(roleArgument);
        List<Player> players = argHandler.get(PLAYERS_ARGUMENT);
        players.forEach(player -> {
            roleManager.setRole(player.getUniqueId(), role);
            chatManager.refreshName(player);
        });
        roleManager.save();
        return CommandResult.success("Gave role &l" + role.getName() + " &ato player(s): &l" + FormatUtils.formatPlayers(players));
    }
}
