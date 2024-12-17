package me.clickism.clickeventlib.commands.debug;

import me.clickism.subcommandapi.command.SubcommandGroup;

/**
 * Subcommand group for debug-related commands.
 */
public class DebugSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new debug subcommand group.
     */
    public DebugSubcommandGroup() {
        super("debug", true);
        addSubcommand(new DebugListSubcommand());
        addSubcommand(new DebugSetSubcommand());
    }
}
