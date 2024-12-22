package me.clickism.clickeventlib.commands.point;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.debug.LocationDisplayer;
import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.PointManager;
import me.clickism.clickeventlib.util.Utils;
import me.clickism.subcommandapi.command.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

class PointDisplaySubcommand extends Subcommand {
    public static final Material DISPLAY_MATERIAL = Material.GREEN_STAINED_GLASS;
    public static final Color DISPLAY_COLOR = Color.LIME;

    private final LocationDisplayer locationDisplayer = new LocationDisplayer();

    private final PointManager pointManager;

    public PointDisplaySubcommand(PointManager pointManager) {
        super("display", true);
        this.pointManager = pointManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        if (locationDisplayer.isDisplaying()) {
            locationDisplayer.removeDisplays();
            return CommandResult.warning("Removed all point displays.");
        }
        pointManager.getPoints().forEach(spawnpoint -> {
            Location location = spawnpoint.getLocation();
            if (location == null) {
                MessageType.WARN.sendSilently(sender, "Point &l" + spawnpoint.getName() + " &7has no location set.");
                return;
            }
            String infoText = getInfoText(spawnpoint);
            locationDisplayer.displayLocation(location, DISPLAY_MATERIAL, DISPLAY_COLOR, infoText);
        });
        return CommandResult.success("Displayed all points.");
    }

    private String getInfoText(EventLocation point) {
        return Utils.colorize("&2&lId: &f&l" + point.getName());
    }
}
