package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerManager;
import org.bukkit.command.CommandSender;

class TriggerInteractionRemoveAllSubcommand extends Subcommand {
    private final TriggerManager triggerManager;
    private final SelectionArgument<Trigger> filterArgument;

    public TriggerInteractionRemoveAllSubcommand(TriggerManager triggerManager) {
        super("remove_all", true);
        this.triggerManager = triggerManager;
        filterArgument = new SelectionArgument<>("trigger", false, triggerManager.getTriggers());
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Trigger trigger = argHandler.getOrNull(filterArgument);
        if (trigger == null) {
            triggerManager.clearTriggerInteractionsAndSave();
            return CommandResult.success("Removed all trigger interactions.");
        }
        triggerManager.clearTriggerInteractionsAndSave(trigger);
        return CommandResult.success("Removed all trigger interactions with the trigger &l" + trigger.getName() + ".");
    }
}
