package me.clickism.clickeventlib.commands.debug;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.debug.DebugPropertyRegistration;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

class DebugListSubcommand extends Subcommand {
    public DebugListSubcommand() {
        super("list", true);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        MessageType.CONFIRM.send(sender, "Debug properties and values:\n" +
                DebugPropertyRegistration.getDebugProperties().stream()
                        .map(property -> "&a- &l" + property.getName() + "&a: &f" + property.get())
                        .collect(Collectors.joining("\n")));
        return CommandResult.success();
    }
}
