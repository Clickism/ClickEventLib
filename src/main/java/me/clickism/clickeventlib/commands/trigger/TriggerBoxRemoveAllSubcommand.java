package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;

class TriggerBoxRemoveAllSubcommand extends Subcommand {
    private final TriggerManager triggerManager;

    private final SelectionArgument<Trigger> triggerArgument;

    public TriggerBoxRemoveAllSubcommand(TriggerManager triggerManager) {
        super("remove_all", true);
        this.triggerManager = triggerManager;
        this.triggerArgument = new SelectionArgument<>("trigger", false, triggerManager.getTriggers());
        addArgument(triggerArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Trigger filter = argHandler.getOrNull(triggerArgument);
        if (filter != null) {
            triggerManager.clearTriggerBoxesAndSave(filter);
            return CommandResult.success("Removed all triggers of type &l" + filter.getName());
        }
        triggerManager.clearTriggerBoxesAndSave();
        return CommandResult.success("Removed all triggers");
    }
}
