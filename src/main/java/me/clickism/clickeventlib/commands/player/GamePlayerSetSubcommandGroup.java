package me.clickism.clickeventlib.commands.player;

import me.clickism.clickeventlib.util.GamePlayerSet;
import me.clickism.subcommandapi.command.SubcommandGroup;

/**
 * A subcommand group for adding and removing players from a {@link GamePlayerSet}.
 */
public class GamePlayerSetSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new game player set subcommand group.
     *
     * @param label     the label of the subcommand group
     * @param playerSet the game player set to add and remove players from
     */
    public GamePlayerSetSubcommandGroup(String label, GamePlayerSet playerSet) {
        super(label, true);
        addSubcommand(new PlayerSetSubcommandGroup("ingame", playerSet.getInGameSet()));
        addSubcommand(new PlayerSetSubcommandGroup("disqualified", playerSet.getDisqualifiedSet()));
    }
}
