package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.subcommandapi.command.CommandException;
import me.clickism.subcommandapi.argument.Argument;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * An argument that parses a world.
 */
class WorldArgument extends Argument<World> {
    private final WorldManager worldManager;

    /**
     * Creates a new world argument.
     *
     * @param name         the name of the argument
     * @param required     whether the argument is required
     * @param worldManager the world manager
     */
    public WorldArgument(String name, boolean required, WorldManager worldManager) {
        super(name, required);
        this.worldManager = worldManager;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        return worldManager.getWorldNames().stream().toList();
    }

    @Override
    public World parse(CommandSender sender, String arg) throws CommandException {
        World world = worldManager.getWorld(arg);
        if (world == null) {
            throw new CommandException("World " + arg + " not found");
        }
        return world;
    }
}
