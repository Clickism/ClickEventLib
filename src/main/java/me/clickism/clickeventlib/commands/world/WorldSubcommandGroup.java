package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.clickeventlib.command.SubcommandGroup;

/**
 * Subcommand group for world-related commands.
 */
public class WorldSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new world subcommand group.
     *
     * @param worldManager the world manager
     */
    public WorldSubcommandGroup(WorldManager worldManager) {
        super("world", true);
        addSubcommand(new WorldGenerateSubcommand(worldManager));
        addSubcommand(new WorldTeleportSubcommand(worldManager));
        addSubcommand(new WorldImportSubcommand(worldManager));
    }
}
