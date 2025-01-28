package me.clickism.clickeventlib.commands.world;

import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.subcommandapi.argument.SelectionArgument;
import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.util.Named;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.command.CommandSender;

class WorldForgetSubcommand extends Subcommand {
    private final SelectionArgument<Named> worldNameArgument;
    private final WorldManager worldManager;

    public WorldForgetSubcommand(WorldManager worldManager) {
        super("forget", true);
        this.worldNameArgument = new SelectionArgument<>("world", true, () -> NamedCollection.of(worldManager.getWorldNames()));
        this.worldManager = worldManager;
        addArgument(worldNameArgument);
    }

    @Override
    protected CommandResult execute(CommandStack commandStack, CommandSender commandSender, ArgumentHandler argumentHandler) throws CommandException {
        String worldName = argumentHandler.get(this.worldNameArgument).getName();
        if (worldManager.forgetWorld(worldName)) {
            return CommandResult.success("World &l" + worldName + "&a has been forgotten.");
        }
        return CommandResult.failure("World &l" + worldName + "&c not found.");
    }
}
