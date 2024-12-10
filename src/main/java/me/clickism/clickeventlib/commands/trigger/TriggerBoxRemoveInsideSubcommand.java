package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerBox;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.clickeventlib.util.FormatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class TriggerBoxRemoveInsideSubcommand extends PlayerOnlySubcommand {
    private static final String ALL_FLAG = "all";
    private final TriggerManager triggerManager;

    private final SelectionArgument<Trigger> triggerArgument;

    public TriggerBoxRemoveInsideSubcommand(TriggerManager triggerManager) {
        super("remove_inside", true);
        this.triggerManager = triggerManager;
        this.triggerArgument = new SelectionArgument<>("trigger", false, triggerManager.getTriggers());
        addArgument(triggerArgument);
        addFlag(ALL_FLAG);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Location location = player.getLocation();
        Collection<TriggerBox> triggerBoxes = triggerManager.getTriggerBoxes();
        if (triggerBoxes == null) {
            return CommandResult.failure("No triggers in this world.");
        }
        Trigger filter = argHandler.getOrNull(triggerArgument);
        boolean all = argHandler.hasFlag(ALL_FLAG);
        Collection<TriggerBox> removed = removeTriggersInside(triggerBoxes, location, filter, all);
        triggerManager.save();
        if (removed.isEmpty()) {
            return CommandResult.failure("No triggers at your location.");
        }
        return CommandResult.success("Removed triggers:" + FormatUtils.formatTriggerBoxes(removed));
    }

    private Collection<TriggerBox> removeTriggersInside(Collection<TriggerBox> triggerBoxes, Location location,
                                                        Trigger filter, boolean all) {
        List<TriggerBox> removed = new ArrayList<>();
        Iterator<TriggerBox> iterator = triggerBoxes.iterator();
        while (iterator.hasNext()) {
            TriggerBox triggerBox = iterator.next();
            if (filter != null && !triggerBox.getTrigger().equals(filter)) {
                continue;
            }
            if (triggerBox.isInside(location)) {
                iterator.remove();
                removed.add(triggerBox);
                if (!all) break;
            }
        }
        return removed;
    }
}
