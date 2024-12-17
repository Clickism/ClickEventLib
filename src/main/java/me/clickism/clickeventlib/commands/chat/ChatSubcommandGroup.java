package me.clickism.clickeventlib.commands.chat;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.subcommandapi.command.SubcommandGroup;

/**
 * Subcommand group for chat-related commands.
 */
public class ChatSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new chat subcommand group.
     *
     * @param chatManager the chat manager
     */
    public ChatSubcommandGroup(ChatManager chatManager) {
        super("chat", true);
        addSubcommand(new ChatAnnounceSubcommand("announce", false));
        addSubcommand(new ChatAnnounceSubcommand("announce_silently", true));
        addSubcommand(new ChatMuteSubcommand(chatManager, ChatMuteSubcommand.MuteAction.MUTE));
        addSubcommand(new ChatMuteSubcommand(chatManager, ChatMuteSubcommand.MuteAction.UNMUTE));
    }
}
