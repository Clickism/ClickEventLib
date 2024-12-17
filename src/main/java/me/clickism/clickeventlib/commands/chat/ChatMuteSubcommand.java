package me.clickism.clickeventlib.commands.chat;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.subcommandapi.argument.EnumArgument;
import me.clickism.subcommandapi.argument.PlayersArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

class ChatMuteSubcommand extends Subcommand {
    enum MuteAction {
        MUTE, UNMUTE
    }

    private static final EnumArgument<ChatManager.ChatType> MUTE_OPTION_ARGUMENT =
            new EnumArgument<>("<voice/text>", true, ChatManager.ChatType.class);
    private static final PlayersArgument PLAYERS_ARGUMENT =
            new PlayersArgument("players", true);

    private final ChatManager chatManager;
    private final MuteAction action;

    public ChatMuteSubcommand(ChatManager chatManager, MuteAction action) {
        super(action.name().toLowerCase(), true);
        this.chatManager = chatManager;
        this.action = action;
        addArgument(MUTE_OPTION_ARGUMENT);
        addArgument(PLAYERS_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        ChatManager.ChatType chatType = argHandler.get(MUTE_OPTION_ARGUMENT);
        Collection<Player> players = argHandler.get(PLAYERS_ARGUMENT);
        return switch (action) {
            case MUTE -> mute(players, chatType);
            case UNMUTE -> unmute(players, chatType);
        };
    }

    private CommandResult mute(Collection<Player> players, ChatManager.ChatType chatType) {
        players.forEach(player -> chatManager.mute(player, chatType));
        return CommandResult.success("&lMuted &a" + chatType.name().toLowerCase() + " chat for players: &l" +
                FormatUtils.formatPlayers(players));
    }

    private CommandResult unmute(Collection<Player> players, ChatManager.ChatType chatType) {
        players.forEach(player -> chatManager.unmute(player, chatType));
        return CommandResult.success("&lUnmuted &a" + chatType.name().toLowerCase() + " chat for players: &l" +
                FormatUtils.formatPlayers(players));
    }
}
