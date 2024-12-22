package me.clickism.clickeventlib.commands.location;

import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.EventLocationManager;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class LocationTeleportSubcommand extends PlayerOnlySubcommand {
    private final SelectionArgument<EventLocation> locationArgument;

    public LocationTeleportSubcommand(EventLocationManager locationManager) {
        super("teleport", true);
        this.locationArgument = new SelectionArgument<>("location", true, locationManager.getEventLocations());
        addArgument(locationArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        EventLocation eventLocation = argHandler.get(locationArgument);
        String locationName = eventLocation.getName();
        Location location = eventLocation.getLocation();
        if (!eventLocation.isLocationSet()) {
            return CommandResult.failure("Location not set for &l" + locationName + "&c.");
        }
        if (!eventLocation.isWorldLoaded()) {
            return CommandResult.failure("World is not loaded for &l" + locationName + "&c.");
        }
        if (location != null) {
            player.teleport(location);
        }
        return CommandResult.success("Teleported to location &l" + locationName + "&a.");
    }
}
