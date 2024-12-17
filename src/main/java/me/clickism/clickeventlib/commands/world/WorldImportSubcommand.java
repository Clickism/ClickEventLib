package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.StringArgument;
import org.bukkit.command.CommandSender;

import java.io.IOException;

class WorldImportSubcommand extends Subcommand {
    private static final StringArgument WORLD_NAME_ARGUMENT = new StringArgument("name", true);

    private final WorldManager worldManager;

    public WorldImportSubcommand(WorldManager worldManager) {
        super("import", false);
        this.worldManager = worldManager;
        addArgument(WORLD_NAME_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        String name = argHandler.get(WORLD_NAME_ARGUMENT);
        try {
            worldManager.importWorld(name);
        } catch (IllegalArgumentException | IOException e) {
            throw new CommandException(e);
        }
        return CommandResult.success("Imported world &f&l" + name);
    }
}
