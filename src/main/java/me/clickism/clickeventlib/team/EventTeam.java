package me.clickism.clickeventlib.team;

import me.clickism.clickeventlib.util.Identifier;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.ChatColor;

/**
 * Represents a team in an event.
 */
public class EventTeam implements Named {

    /**
     * The setting for who can join the team.
     */
    public enum FriendlyFireSetting {
        /**
         * Use the default setting.
         */
        USE_DEFAULT,
        /**
         * Friendly fire is allowed.
         */
        ALLOWED,
        /**
         * Friendly fire is not allowed.
         */
        NOT_ALLOWED
    }

    private static final String DEFAULT_PREFIX = "[👥] " + ChatColor.RESET;

    /**
     * Red team.
     */
    public static final EventTeam RED = new EventTeam("red", ChatColor.RED);
    /**
     * Blue team.
     */
    public static final EventTeam BLUE = new EventTeam("blue", ChatColor.BLUE);
    /**
     * Green team.
     */
    public static final EventTeam GREEN = new EventTeam("green", ChatColor.GREEN);
    /**
     * Yellow team.
     */
    public static final EventTeam YELLOW = new EventTeam("yellow", ChatColor.YELLOW);
    /**
     * Orange team.
     */
    public static final EventTeam ORANGE = new EventTeam("orange", ChatColor.GOLD);
    /**
     * Anthracite team.
     */
    public static final EventTeam ANTHRACITE = new EventTeam("anthracite", ChatColor.DARK_GRAY);
    /**
     * Gray team.
     */
    public static final EventTeam GRAY = new EventTeam("gray", ChatColor.GRAY);
    /**
     * Cyan team.
     */
    public static final EventTeam CYAN = new EventTeam("cyan", ChatColor.AQUA);
    /**
     * Moss team.
     */
    public static final EventTeam MOSS = new EventTeam("moss", ChatColor.DARK_GREEN);
    /**
     * Purple team.
     */
    public static final EventTeam PURPLE = new EventTeam("purple", ChatColor.DARK_PURPLE);
    /**
     * Blood team.
     */
    public static final EventTeam BLOOD = new EventTeam("blood", ChatColor.DARK_RED);
    /**
     * Ocean team.
     */
    public static final EventTeam OCEAN = new EventTeam("ocean", ChatColor.DARK_AQUA);

    private final String name;
    private final ChatColor color;
    private final String prefix;

    private final JoinSetting joinSetting;

    private final FriendlyFireSetting friendlyFireSetting;

    /**
     * Creates a new team with the given id and color.
     * Uses {@link JoinSetting#USE_DEFAULT} and {@link FriendlyFireSetting#USE_DEFAULT}.
     *
     * @param id    the id of the team
     * @param color the color of the team
     */
    public EventTeam(Identifier id, ChatColor color) {
        this(id, color, DEFAULT_PREFIX, JoinSetting.USE_DEFAULT, FriendlyFireSetting.USE_DEFAULT);
    }

    /**
     * Creates a new team with the given name and color.
     * Uses {@link JoinSetting#USE_DEFAULT} and {@link FriendlyFireSetting#USE_DEFAULT}.
     *
     * @param name  the name of the team
     * @param color the color of the team
     */
    public EventTeam(String name, ChatColor color) {
        this(name, color, DEFAULT_PREFIX, JoinSetting.USE_DEFAULT, FriendlyFireSetting.USE_DEFAULT);
    }

    /**
     * Creates a new team with the given id, color, team setting, and friendly fire setting.
     *
     * @param id                  the id of the team
     * @param color               the color of the team
     * @param joinSetting         the team setting of the team
     * @param friendlyFireSetting whether friendly fire is allowed
     */
    public EventTeam(Identifier id, ChatColor color, JoinSetting joinSetting, FriendlyFireSetting friendlyFireSetting) {
        this(id, color, DEFAULT_PREFIX, joinSetting, friendlyFireSetting);
    }

    /**
     * Creates a new team with the given name, color, team setting, and friendly fire setting.
     *
     * @param name                the name of the team
     * @param color               the color of the team
     * @param joinSetting         the team setting of the team
     * @param friendlyFireSetting whether friendly fire is allowed
     */
    public EventTeam(String name, ChatColor color, JoinSetting joinSetting, FriendlyFireSetting friendlyFireSetting) {
        this(name, color, DEFAULT_PREFIX, joinSetting, friendlyFireSetting);
    }

    /**
     * Creates a new team with the given id, color, prefix, team setting, and friendly fire setting.
     *
     * @param id                  the id of the team
     * @param color               the color of the team
     * @param prefix              the prefix of the team
     * @param joinSetting         the team setting of the team
     * @param friendlyFireSetting whether friendly fire is allowed
     */
    public EventTeam(Identifier id, ChatColor color, String prefix, JoinSetting joinSetting, FriendlyFireSetting friendlyFireSetting) {
        this(id.toString(), color, prefix, joinSetting, friendlyFireSetting);
    }

    /**
     * Creates a new team with the given name, color, prefix, team setting, and friendly fire setting.
     *
     * @param name                the name of the team
     * @param color               the color of the team
     * @param prefix              the prefix of the team
     * @param joinSetting         the team setting of the team
     * @param friendlyFireSetting whether friendly fire is allowed
     */
    public EventTeam(String name, ChatColor color, String prefix, JoinSetting joinSetting, FriendlyFireSetting friendlyFireSetting) {
        this.name = name;
        this.color = color;
        this.prefix = color + prefix;
        this.joinSetting = joinSetting;
        this.friendlyFireSetting = friendlyFireSetting;
    }

    /**
     * Gets the team setting for this team.
     *
     * @param defaultJoinSetting the default team setting to use if this team uses {@link JoinSetting#USE_DEFAULT}
     * @return the team setting for this team
     */
    public JoinSetting getJoinSetting(JoinSetting defaultJoinSetting) {
        if (joinSetting == JoinSetting.USE_DEFAULT) {
            return defaultJoinSetting;
        }
        return joinSetting;
    }

    /**
     * Returns whether friendly fire is allowed for this team.
     *
     * @param defaultAllowFriendlyFire the default friendly fire setting to use if this team uses {@link FriendlyFireSetting#USE_DEFAULT}
     * @return whether friendly fire is allowed for this team
     */
    public boolean isFriendlyFireAllowed(boolean defaultAllowFriendlyFire) {
        if (friendlyFireSetting == FriendlyFireSetting.USE_DEFAULT) {
            return defaultAllowFriendlyFire;
        }
        return friendlyFireSetting == FriendlyFireSetting.ALLOWED;
    }

    /**
     * Gets the color of this team.
     *
     * @return the color of this team
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Gets the prefix of this team.
     *
     * @return the prefix of this team
     */
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Registers all default teams with the given team manager.
     *
     * @param teamManager the team manager to register the teams with
     */
    public static void registerTeams(TeamManager teamManager) {
        teamManager.registerTeam(RED);
        teamManager.registerTeam(BLUE);
        teamManager.registerTeam(GREEN);
        teamManager.registerTeam(YELLOW);
        teamManager.registerTeam(ORANGE);
        teamManager.registerTeam(ANTHRACITE);
        teamManager.registerTeam(GRAY);
        teamManager.registerTeam(CYAN);
        teamManager.registerTeam(MOSS);
        teamManager.registerTeam(PURPLE);
        teamManager.registerTeam(BLOOD);
        teamManager.registerTeam(OCEAN);
    }
}
