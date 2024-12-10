package me.clickism.clickeventlib.commands.debug;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.Argument;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.command.argument.StringArgument;
import me.clickism.clickeventlib.debug.DebugPropertyRegistration;
import me.clickism.clickeventlib.property.Property;
import org.bukkit.command.CommandSender;

import java.util.List;

class DebugSetSubcommand extends Subcommand {
    private static final SelectionArgument<Property<?>> DEBUG_PROPERTY_ARGUMENT =
            new SelectionArgument<>("property", true, DebugPropertyRegistration.getDebugProperties());
    private static final StringArgument VALUE_ARGUMENT = new StringArgument("value", true);

    public DebugSetSubcommand() {
        super("set", true);
        addArgument(DEBUG_PROPERTY_ARGUMENT);
        addArgument(VALUE_ARGUMENT);
    }

    @Override
    public List<Argument<?>> getArguments() {
        return List.of(DEBUG_PROPERTY_ARGUMENT, VALUE_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Property<?> property = argHandler.get(DEBUG_PROPERTY_ARGUMENT);
        try {
            property.parseAndSet(argHandler.get(VALUE_ARGUMENT));
        } catch (IllegalArgumentException exception) {
            return CommandResult.failure("Invalid value: &l" + argHandler.get(VALUE_ARGUMENT));
        }
        return CommandResult.success("Set &l" + property.getName() + "&a to: &f&l" + property.get());
    }
}
