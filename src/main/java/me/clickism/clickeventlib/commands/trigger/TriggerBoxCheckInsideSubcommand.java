package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.trigger.TriggerBox;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.subcommandapi.command.*;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TriggerBoxCheckInsideSubcommand extends PlayerOnlySubcommand {
    private final TriggerManager triggerManager;

    public TriggerBoxCheckInsideSubcommand(TriggerManager triggerManager) {
        super("check_inside", true);
        this.triggerManager = triggerManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Collection<TriggerBox> triggerBoxes = triggerManager.getTriggerBoxes();
        if (triggerBoxes == null) {
            return CommandResult.failure("No triggers in this world.");
        }
        List<TriggerBox> inside = new ArrayList<>();
        for (TriggerBox triggerBox : triggerBoxes) {
            if (triggerBox.isInside(player.getLocation())) {
                inside.add(triggerBox);
            }
        }
        if (inside.isEmpty()) {
            return CommandResult.failure("No triggers at your location.");
        } else {
            return CommandResult.success("You are inside " + inside.size() + " trigger(s):" +
                                         FormatUtils.formatTriggerBoxes(inside));
        }
    }
}
