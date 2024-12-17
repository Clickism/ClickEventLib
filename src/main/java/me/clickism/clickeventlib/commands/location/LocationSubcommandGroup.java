package me.clickism.clickeventlib.commands.location;

import me.clickism.subcommandapi.command.SubcommandGroup;
import me.clickism.clickeventlib.location.EventLocationManager;

/**
 * Subcommand group for location-related commands.
 */
public class LocationSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new location subcommand group.
     *
     * @param locationManager the location manager
     */
    public LocationSubcommandGroup(EventLocationManager locationManager) {
        super("location", true);
        addSubcommand(new LocationSetSubcommand(locationManager));
        addSubcommand(new LocationTeleportSubcommand(locationManager));
        addSubcommand(new LocationListSubcommand(locationManager));
    }
}
