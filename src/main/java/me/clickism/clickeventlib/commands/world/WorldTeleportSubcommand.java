package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.subcommandapi.command.*;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class WorldTeleportSubcommand extends Subcommand {
    private final WorldArgument worldArgument;

    public WorldTeleportSubcommand(WorldManager worldManager) {
        super("teleport", false);
        this.worldArgument = new WorldArgument("world", true, worldManager);
        addArgument(worldArgument);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        World world = argHandler.get(worldArgument);
        if (!(sender instanceof Player player)) {
            return CommandResult.failure("Only players can teleport.");
        }
        player.teleport(world.getSpawnLocation());
        return CommandResult.success("Teleported to world &f&l" + world.getName());
    }
}
