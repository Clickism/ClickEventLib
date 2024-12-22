package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.subcommandapi.argument.EnumArgument;
import me.clickism.subcommandapi.argument.LongArgument;
import me.clickism.subcommandapi.argument.StringArgument;
import me.clickism.subcommandapi.command.*;
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
    private static final LongArgument SEED_ARGUMENT = new LongArgument("seed", false);
    
    private final WorldManager worldManager;

    public WorldGenerateSubcommand(WorldManager worldManager) {
        super("generate", false);
        this.worldManager = worldManager;
        addArgument(WORLD_TYPE_ARGUMENT);
        addArgument(WORLD_NAME_ARGUMENT);
        addArgument(ENVIRONMENT_ARGUMENT);
        addArgument(SEED_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        WorldType type = argHandler.get(WORLD_TYPE_ARGUMENT);
        String name = argHandler.get(WORLD_NAME_ARGUMENT);
        Long seed = argHandler.getOrNull(SEED_ARGUMENT);
        World.Environment environment = argHandler.getOrDefault(ENVIRONMENT_ARGUMENT, World.Environment.NORMAL);
        WorldCreator creator = new WorldCreator(name)
                .type(type)
                .environment(environment);
        if (seed != null) {
            creator.seed(seed);
        }
        try {
            worldManager.generateWorld(creator);
        } catch (IllegalArgumentException | IOException e) {
            throw new CommandException(e);
        }
        return CommandResult.success("Generated world &f&l" + name.toLowerCase() + "&a with type &l" + 
                                     type.toString().toLowerCase() + "&a and environment &l" + environment.toString().toLowerCase() + 
                                     "&a and seed &l" + (seed == null ? "random" : seed) + "&a.");
    }
}
