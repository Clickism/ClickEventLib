package me.clickism.clickeventlib.commands;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.subcommandapi.command.CommandManager;
import me.clickism.subcommandapi.command.CommandResult;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * A command manager that sends messages using {@link MessageType}.
 */
public class EventCommandManager extends CommandManager {
    /**
     * Create a new event command manager.
     */
    public EventCommandManager() {
        super();
    }

    @Override
    protected void sendMessage(CommandSender sender, CommandResult.CommandResultType resultType, @NotNull String message) {
        switch (resultType) {
            case SUCCESS -> MessageType.CONFIRM.send(sender, message);
            case FAILURE -> MessageType.FAIL.send(sender, message);
            case WARNING -> MessageType.WARN.send(sender, message);
        }
    }
}
