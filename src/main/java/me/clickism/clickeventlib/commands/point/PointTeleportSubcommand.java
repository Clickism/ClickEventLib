package me.clickism.clickeventlib.commands.point;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.PointManager;
import org.bukkit.entity.Player;

class PointTeleportSubcommand extends PlayerOnlySubcommand {
    private final SelectionArgument<EventLocation> pointArgument;

    public PointTeleportSubcommand(PointManager spawnpointManager) {
        super("teleport", true);
        this.pointArgument = new SelectionArgument<>("point", true, spawnpointManager.getPoints());
        addArgument(pointArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        EventLocation point = argHandler.get(pointArgument);
        String pointName = point.getName();
        if (!point.teleportIfSet(player)) {
            return CommandResult.failure("Point &l" + pointName + " &chas no location set.");
        }
        return CommandResult.success("Teleported to point &l" + pointName + ".");
    }
}
