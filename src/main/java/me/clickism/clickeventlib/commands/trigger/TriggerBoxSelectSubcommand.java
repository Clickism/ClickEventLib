package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.trigger.TriggerBox;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.clickeventlib.trigger.TriggerSelectionManager;
import me.clickism.subcommandapi.argument.EnumArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class TriggerBoxSelectSubcommand extends PlayerOnlySubcommand {
    private static final EnumArgument<TriggerSelectionManager.PosType> POS_TYPE_ARGUMENT =
            new EnumArgument<>("pos_type", true, TriggerSelectionManager.PosType.class);

    private final TriggerManager triggerManager;

    public TriggerBoxSelectSubcommand(TriggerManager triggerManager) {
        super("select_pos", true);
        this.triggerManager = triggerManager;
        addArgument(POS_TYPE_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        TriggerSelectionManager.PosType posType = argHandler.get(POS_TYPE_ARGUMENT);
        Location location = player.getLocation();
        TriggerBox box;
        try {
            box = triggerManager.getSelectionManager().selectPosAndBuild(player, location, posType, triggerManager);
        } catch (Exception exception) {
            return CommandResult.failure("You aren't selecting a trigger box.");
        }
        String posName = posType.name().toLowerCase();
        if (box == null) {
            return CommandResult.success("Selected position: &l" + posName + ".");
        }
        String name = box.getName();
        return CommandResult.success("Trigger created: &l" + name);
    }
}
