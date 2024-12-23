package me.clickism.clickeventlib.commands.player;

import me.clickism.clickeventlib.util.PlayerSet;
import me.clickism.subcommandapi.command.SubcommandGroup;

/**
 * A subcommand group for adding and removing players from a {@link PlayerSet}.
 */
public class PlayerSetSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new player set subcommand group.
     *
     * @param label     the label of the subcommand group
     * @param playerSet the player set to add and remove players from
     */
    public PlayerSetSubcommandGroup(String label, PlayerSet playerSet) {
        super(label, true);
        addSubcommand(new PlayerSetAddSubcommand(playerSet));
        addSubcommand(new PlayerSetRemoveSubcommand(playerSet));
    }
}
