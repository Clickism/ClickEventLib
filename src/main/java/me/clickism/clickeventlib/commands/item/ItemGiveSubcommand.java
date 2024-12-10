package me.clickism.clickeventlib.commands.item;

import me.clickism.clickeventlib.command.*;
import me.clickism.clickeventlib.command.argument.IntegerArgument;
import me.clickism.clickeventlib.command.argument.PlayersArgument;
import me.clickism.clickeventlib.command.argument.SelectionArgument;
import me.clickism.clickeventlib.item.CustomItem;
import me.clickism.clickeventlib.item.CustomItemManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

class ItemGiveSubcommand extends Subcommand {
    private static final PlayersArgument PLAYERS_ARGUMENT = new PlayersArgument("players", true);
    private static final IntegerArgument AMOUNT_ARGUMENT = new IntegerArgument("amount", false);

    private final SelectionArgument<CustomItem> itemArgument;

    public ItemGiveSubcommand(CustomItemManager itemManager) {
        super("give", true);
        this.itemArgument = new SelectionArgument<>("item", true, itemManager.getItems());
        addArgument(PLAYERS_ARGUMENT);
        addArgument(itemArgument);
        addArgument(AMOUNT_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, CommandSender sender, ArgumentHandler argHandler) throws CommandException {
        Collection<Player> players = argHandler.get(PLAYERS_ARGUMENT);
        CustomItem item = argHandler.get(itemArgument);
        Integer amount = argHandler.getOrDefault(AMOUNT_ARGUMENT, 1);
        players.forEach(player -> Utils.addItem(player, item.createItem(amount)));
        return CommandResult.success("Gave &f&l" + amount + " " + item.getDisplayName() + "&a to players: &l" + FormatUtils.formatPlayers(players));
    }
}
