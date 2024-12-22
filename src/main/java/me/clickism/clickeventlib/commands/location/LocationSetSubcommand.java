package me.clickism.clickeventlib.commands.location;

import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.EventLocationManager;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class LocationSetSubcommand extends PlayerOnlySubcommand {
    private final EventLocationManager locationManager;
    private final SelectionArgument<EventLocation> locationArgument;

    public LocationSetSubcommand(EventLocationManager locationManager) {
        super("set", true);
        this.locationManager = locationManager;
        this.locationArgument = new SelectionArgument<>("location", true, locationManager.getEventLocations());
        addArgument(locationArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        EventLocation eventLocation = argHandler.get(locationArgument);
        String worldName = player.getWorld().getName();
        Location location = player.getLocation();
        eventLocation.setLocation(worldName, location);
        locationManager.save();
        return CommandResult.success("Location &l" + eventLocation.getName() + "&a set to your current location.");
    }
}
