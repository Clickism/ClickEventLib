package me.clickism.clickeventlib.commands.chat;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.subcommandapi.argument.StringArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;

class ChatAnnounceSubcommand extends Subcommand {
    private static final StringArgument MESSAGE_ARGUMENT = new StringArgument("message", true);

    private final boolean silent;

    public ChatAnnounceSubcommand(String name, boolean silent) {
        super(name, true);
        this.silent = silent;
        addArgument(MESSAGE_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        String message = String.join(" ", argHandler.getArgs());
        if (silent) {
            MessageType.ANNOUNCE.sendAllSilently(message);
        } else {
            MessageType.ANNOUNCE.sendAll(message);
        }
        return CommandResult.success();
    }
}
