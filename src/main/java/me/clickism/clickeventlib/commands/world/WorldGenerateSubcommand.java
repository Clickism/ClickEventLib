package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.EnumArgument;
import me.clickism.clickeventlib.command.argument.StringArgument;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;

import java.io.IOException;

class WorldGenerateSubcommand extends Subcommand {
    private static final EnumArgument<WorldType> WORLD_TYPE_ARGUMENT =
            new EnumArgument<>("type", true, WorldType.class);
    private static final StringArgument WORLD_NAME_ARGUMENT =
            new StringArgument("name", true);
    private static final EnumArgument<World.Environment> ENVIRONMENT_ARGUMENT =
            new EnumArgument<>("environment", false, World.Environment.class);

    private final WorldManager worldManager;

    public WorldGenerateSubcommand(WorldManager worldManager) {
        super("generate", false);
        this.worldManager = worldManager;
        addArgument(WORLD_TYPE_ARGUMENT);
        addArgument(WORLD_NAME_ARGUMENT);
        addArgument(ENVIRONMENT_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        WorldType type = argHandler.get(WORLD_TYPE_ARGUMENT);
        String name = argHandler.get(WORLD_NAME_ARGUMENT);
        World.Environment environment = argHandler.getOrDefault(ENVIRONMENT_ARGUMENT, World.Environment.NORMAL);
        try {
            worldManager.generateWorld(new WorldCreator(name)
                    .type(type)
                    .environment(environment));
        } catch (IllegalArgumentException | IOException e) {
            throw new CommandException(e);
        }
        return CommandResult.success("Generated world &f&l" + name + "&a with type &l" + type + "&a and environment &l" + environment);
    }
}
