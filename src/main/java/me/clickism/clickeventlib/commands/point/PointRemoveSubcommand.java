package me.clickism.clickeventlib.commands.point;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.PointManager;
import org.bukkit.command.CommandSender;

class PointRemoveSubcommand extends Subcommand {
    private final SelectionArgument<EventLocation> pointArgument;
    private final PointManager pointManager;
    public PointRemoveSubcommand(PointManager pointManager) {
        super("remove", true);
        this.pointManager = pointManager;
        this.pointArgument = new SelectionArgument<>("point", true, pointManager.getPoints());
        addArgument(pointArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        EventLocation point = argHandler.get(pointArgument);
        pointManager.removePoint(point);
        return CommandResult.warning("Removed point &l" + point.getName() + ".");
    }
}
