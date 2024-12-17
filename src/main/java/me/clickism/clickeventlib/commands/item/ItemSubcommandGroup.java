package me.clickism.clickeventlib.commands.item;

import me.clickism.subcommandapi.command.SubcommandGroup;
import me.clickism.clickeventlib.item.CustomItemManager;

/**
 * Subcommand group for item-related commands.
 */
public class ItemSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new item subcommand group.
     *
     * @param itemManager the item manager
     */
    public ItemSubcommandGroup(CustomItemManager itemManager) {
        super("item", true);
        addSubcommand(new ItemGiveSubcommand(itemManager));
    }
}
