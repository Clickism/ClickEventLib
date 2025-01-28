package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.clickeventlib.trigger.TriggerSelectionManager;
import me.clickism.clickeventlib.util.Utils;
import me.clickism.subcommandapi.argument.IntegerArgument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class TriggerBoxAddSubcommand extends PlayerOnlySubcommand {
    private static final IntegerArgument Z_ARGUMENT = new IntegerArgument("z-index", true);

    private final SelectionArgument<Trigger> triggerArgument;

    private final TriggerManager triggerManager;

    public TriggerBoxAddSubcommand(TriggerManager triggerManager) {
        super("add", true);
        this.triggerArgument = new SelectionArgument<>("trigger", true, triggerManager.getTriggers());
        this.triggerManager = triggerManager;
        addArgument(triggerArgument);
        addArgument(Z_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Trigger trigger = argHandler.get(triggerArgument);
        Integer z = argHandler.get(Z_ARGUMENT);
        TriggerSelectionManager selectionManager = triggerManager.getSelectionManager();
        selectionManager.startSelectingBox(player, trigger, z);
        ItemStack boxSelectorItem = triggerManager.getBoxSelectorItem().createItem();
        Utils.addItemIfNotHas(player, boxSelectorItem);
        @SuppressWarnings("DataFlowIssue")
        String itemName = boxSelectorItem.getItemMeta().getDisplayName();
        String triggerName = trigger.getName();
        return CommandResult.success(
                "Selecting trigger &l" + triggerName + ":\n" +
                        "&aUse the " + itemName + "&a to create a trigger."
        );
    }
}
