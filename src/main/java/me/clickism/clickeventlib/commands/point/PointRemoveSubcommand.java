package me.clickism.clickeventlib.commands.point;

import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.PointManager;
import me.clickism.subcommandapi.argument.MultipleSelectionArgument;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.command.CommandSender;

import java.util.List;

class PointRemoveSubcommand extends Subcommand {
    private final MultipleSelectionArgument<EventLocation> pointArgument;
    private final PointManager pointManager;

    public PointRemoveSubcommand(PointManager pointManager) {
        super("remove", true);
        this.pointManager = pointManager;
        this.pointArgument = new MultipleSelectionArgument<>("point", true, pointManager.getPoints());
        addArgument(pointArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        List<EventLocation> eventLocations = argHandler.get(pointArgument);
        eventLocations.forEach(pointManager::removePoint);
        String formattedPoints = eventLocations.stream().map(EventLocation::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("&c&lNone");
        return CommandResult.warning("Removed points &l" + formattedPoints + ".");
    }
}
