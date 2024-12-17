package me.clickism.clickeventlib.commands.trigger;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.clickeventlib.trigger.TriggerBox;
import me.clickism.clickeventlib.trigger.TriggerManager;
import org.bukkit.command.CommandSender;

class TriggerBoxRemoveSubcommand extends Subcommand {
    private final SelectionArgument<TriggerBox> triggerBoxArgument;
    private final TriggerManager triggerManager;

    public TriggerBoxRemoveSubcommand(TriggerManager triggerManager) {
        super("remove", true);
        this.triggerManager = triggerManager;
        this.triggerBoxArgument = new SelectionArgument<>("trigger_box", true, triggerManager.getTriggerBoxes());
        addArgument(triggerBoxArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        TriggerBox box = argHandler.get(triggerBoxArgument);
        triggerManager.unregisterTriggerBox(box);
        return CommandResult.success("Removed trigger box " + box.getName());
    }
}
