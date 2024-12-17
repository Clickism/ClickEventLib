package me.clickism.clickeventlib.commands.point;

import me.clickism.subcommandapi.command.SubcommandGroup;
import me.clickism.clickeventlib.location.PointManager;

/**
 * Subcommand group for point commands.
 */
public class PointSubcommandGroup extends SubcommandGroup {
    /**
     * Create a new point subcommand group.
     *
     * @param pointManager the point manager.
     */
    public PointSubcommandGroup(PointManager pointManager) {
        super(pointManager.getPointNamespace(), true);
        addSubcommand(new PointAddSubcommand(pointManager));
        addSubcommand(new PointRemoveSubcommand(pointManager));
        addSubcommand(new PointDisplaySubcommand(pointManager));
        addSubcommand(new PointTeleportSubcommand(pointManager));
    }
}
