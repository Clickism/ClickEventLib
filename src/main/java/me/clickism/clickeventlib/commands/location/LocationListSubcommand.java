package me.clickism.clickeventlib.commands.location;

import me.clickism.clickeventlib.location.EventLocationManager;
import me.clickism.subcommandapi.command.*;
import org.bukkit.entity.Player;

class LocationListSubcommand extends PlayerOnlySubcommand {
    private final EventLocationManager locationManager;

    public LocationListSubcommand(EventLocationManager locationManager) {
        super("list", true);
        this.locationManager = locationManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        StringBuilder builder = new StringBuilder("&a&lEvent locations:");
        locationManager.getEventLocations().forEach(location -> {
            builder.append("\n&a- ");
            if (location.isLocationSet()) {
                builder.append(location.getName());
            } else {
                builder.append("&c&l").append(location.getName());
            }
        });
        return CommandResult.success(builder.toString());
    }
}
