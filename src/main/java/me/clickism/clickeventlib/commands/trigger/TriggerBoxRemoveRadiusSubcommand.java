package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.DoubleArgument;
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

class TriggerBoxRemoveRadiusSubcommand extends PlayerOnlySubcommand {
    private static final DoubleArgument RADIUS_ARGUMENT = new DoubleArgument("radius", true);
    private static final String ALL_FLAG = "all";
    private final TriggerManager triggerManager;

    private final SelectionArgument<Trigger> triggerArgument;

    public TriggerBoxRemoveRadiusSubcommand(TriggerManager triggerManager) {
        super("remove_radius", true);
        this.triggerManager = triggerManager;
        this.triggerArgument = new SelectionArgument<>("trigger", false, triggerManager.getTriggers());
        addArgument(RADIUS_ARGUMENT);
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
        double radius = argHandler.get(RADIUS_ARGUMENT);
        List<TriggerBox> removed = removeTriggersWithinRadius(triggerBoxes, location, radius, filter, argHandler.hasFlag(ALL_FLAG));
        triggerManager.save();
        if (removed.isEmpty()) {
            return CommandResult.failure("No triggers within radius &l" + radius + ".");
        } else {
            return CommandResult.success("Removed " + removed.size() + " triggers within " + radius + " blocks:" +
                    FormatUtils.formatTriggerBoxes(removed));
        }
    }

    private List<TriggerBox> removeTriggersWithinRadius(Collection<TriggerBox> triggerBoxes, Location location, double radius, Trigger filter, boolean removeAll) {
        Iterator<TriggerBox> iterator = triggerBoxes.iterator();
        List<TriggerBox> removed = new ArrayList<>();
        while (iterator.hasNext()) {
            TriggerBox triggerBox = iterator.next();
            if (filter != null && !triggerBox.getTrigger().equals(filter)) {
                continue;
            }
            if (triggerBox.distanceTo(location) <= radius) {
                iterator.remove();
                if (!removeAll) {
                    removed.add(triggerBox);
                    break;
                }
                removed.add(triggerBox);
            }
        }
        return removed;
    }
}
