package me.clickism.clickeventlib.commands.role;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.subcommandapi.command.SubcommandGroup;
import me.clickism.clickeventlib.team.RoleManager;

/**
 * Subcommand group for role management.
 */
public class RoleSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new role subcommand group.
     *
     * @param roleManager the role manager to use
     * @param chatManager the chat manager to use
     */
    public RoleSubcommandGroup(RoleManager roleManager, ChatManager chatManager) {
        super("role", true);
        addSubcommand(new RoleSetSubcommand(roleManager, chatManager));
        addSubcommand(new RoleRemoveSubcommand(roleManager, chatManager));
    }
}
