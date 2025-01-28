package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.clickeventlib.trigger.TriggerSelectionManager;
import me.clickism.clickeventlib.util.Utils;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class TriggerInteractionAddSubcommand extends PlayerOnlySubcommand {
    private final SelectionArgument<Trigger> triggerArgument;

    private final TriggerManager triggerManager;

    public TriggerInteractionAddSubcommand(TriggerManager triggerManager) {
        super("add", true);
        this.triggerArgument = new SelectionArgument<>("trigger", true, triggerManager.getTriggers());
        this.triggerManager = triggerManager;
        addArgument(triggerArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Trigger trigger = argHandler.get(triggerArgument);
        TriggerSelectionManager selectionManager = triggerManager.getSelectionManager();
        selectionManager.startSelectingInteraction(player, trigger);
        ItemStack interactionSelectorItem = triggerManager.getInteractionSelectorItem().createItem();
        Utils.addItemIfNotHas(player, interactionSelectorItem);
        @SuppressWarnings("DataFlowIssue")
        String itemName = interactionSelectorItem.getItemMeta().getDisplayName();
        String triggerName = trigger.getName();
        return CommandResult.success(
                "Selecting trigger &l" + triggerName + ":\n" +
                        "&aUse the " + itemName + "&a to create a trigger."
        );
    }

}
