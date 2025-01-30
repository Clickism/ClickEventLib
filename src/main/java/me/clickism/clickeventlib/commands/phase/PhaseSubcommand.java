package me.clickism.clickeventlib.commands.phase;

import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.subcommandapi.command.CommandException;
import me.clickism.subcommandapi.command.Subcommand;

import java.util.Collection;
import java.util.List;

abstract class PhaseSubcommand extends Subcommand {

    public PhaseSubcommand(String label, boolean requiresOp) {
        super(label, requiresOp);
    }

    protected void validateEventLocations(Collection<EventLocation> locations) throws CommandException {
        List<EventLocation> unsetLocations = locations.stream()
                .filter(l -> !l.isLocationSet())
                .toList();
        if (unsetLocations.isEmpty()) return;
        throw new CommandException("Not all required event locations are set. Use &l--force &cto force the phase change: &l" +
                FormatUtils.formatNamedCollection(unsetLocations));
    }
}
