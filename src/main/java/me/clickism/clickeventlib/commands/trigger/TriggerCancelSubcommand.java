package me.clickism.clickeventlib.commands.trigger;

import me.clickism.subcommandapi.command.*;
import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerSelectionManager;
import org.bukkit.entity.Player;

class TriggerCancelSubcommand extends PlayerOnlySubcommand {

    private final TriggerSelectionManager triggerSelectionManager;

    public TriggerCancelSubcommand(TriggerSelectionManager triggerSelectionManager) {
        super("cancel", true);
        this.triggerSelectionManager = triggerSelectionManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Trigger trigger = triggerSelectionManager.stopSelecting(player);
        if (trigger == null) {
            return CommandResult.failure("You are not currently selecting a trigger.");
        }
        return CommandResult.success("Cancelled trigger selection for &f&l" + trigger.getName());
    }
}
