package me.clickism.clickeventlib.commands.point;

import me.clickism.clickeventlib.location.PointManager;
import me.clickism.clickeventlib.util.Utils;
import me.clickism.subcommandapi.command.*;
import org.bukkit.entity.Player;

class PointAddSubcommand extends PlayerOnlySubcommand {
    private final PointManager pointManager;

    public PointAddSubcommand(PointManager pointManager) {
        super("add", true);
        this.pointManager = pointManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Utils.addItemIfNotHas(player, pointManager.getAdderItem().createItem());
        Utils.addItemIfNotHas(player, pointManager.getSelectorItem().createItem());
        return CommandResult.success("Use the given items to add points.");
    }
}
