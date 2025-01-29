package me.clickism.clickeventlib.team;

import me.clickism.clickeventlib.util.Identifier;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.Objects;

/**
 * Represents a team in an event.
 */
public class EventTeam implements Named {

    /**
     * The setting for who can join the team.
     */
    public enum FriendlyFireSetting {
        /**
         * Friendly fire is allowed.
         */
        ALLOWED,
        /**
         * Friendly fire is not allowed.
         */
        NOT_ALLOWED
    }

    /**
     * Default prefix for teams.
     */
    public static final String DEFAULT_PREFIX = "[👥] " + ChatColor.RESET;

    /**
     * Red team.
     */
    public static final EventTeam RED = new EventTeam("red").setColor(ChatColor.RED);
    /**
     * Blue team.
     */
    public static final EventTeam BLUE = new EventTeam("blue").setColor(ChatColor.BLUE);
    /**
     * Green team.
     */
    public static final EventTeam GREEN = new EventTeam("green").setColor(ChatColor.GREEN);
    /**
     * Yellow team.
     */
    public static final EventTeam YELLOW = new EventTeam("yellow").setColor(ChatColor.YELLOW);
    /**
     * Orange team.
     */
    public static final EventTeam ORANGE = new EventTeam("orange").setColor(ChatColor.GOLD);
    /**
     * Anthracite team.
     */
    public static final EventTeam ANTHRACITE = new EventTeam("anthracite").setColor(ChatColor.DARK_GRAY);
    /**
     * Gray team.
     */
    public static final EventTeam GRAY = new EventTeam("gray").setColor(ChatColor.GRAY);
    /**
     * Cyan team.
     */
    public static final EventTeam CYAN = new EventTeam("cyan").setColor(ChatColor.AQUA);
    /**
     * Moss team.
     */
    public static final EventTeam MOSS = new EventTeam("moss").setColor(ChatColor.DARK_GREEN);
    /**
     * Purple team.
     */
    public static final EventTeam PURPLE = new EventTeam("purple").setColor(ChatColor.DARK_PURPLE);
    /**
     * Blood team.
     */
    public static final EventTeam BLOOD = new EventTeam("blood").setColor(ChatColor.DARK_RED);
    /**
     * Ocean team.
     */
    public static final EventTeam OCEAN = new EventTeam("ocean").setColor(ChatColor.DARK_AQUA);

    private final String name;
    private ChatColor color = ChatColor.RESET;
    private String prefix = DEFAULT_PREFIX;

    private JoinSetting joinSettingOverride = null;
    private Boolean friendlyFireOverride = null;

    /**
     * Creates a new team with the given name and registers it with the team manager.
     *
     * @param name the name of the team
     */
    protected EventTeam(String name) {
        this.name = name;
        TeamManager.INSTANCE.registerTeam(this);
    }

    /**
     * Joins a player to this team.
     *
     * @param player the player to join
     */
    public void join(OfflinePlayer player) {
        join(player.getName());
    }

    /**
     * Joins an entry to this team.
     *
     * @param entry the entry to join
     */
    public void join(String entry) {
        TeamManager.INSTANCE.joinTeam(entry, this);
    }

    /**
     * Leaves a player from this team.
     *
     * @param player the player to leave
     */
    public void leave(OfflinePlayer player) {
        leave(player.getName());
    }

    /**
     * Leaves an entry from this team.
     *
     * @param entry the entry to leave
     */
    public void leave(String entry) {
        TeamManager.INSTANCE.leaveTeam(entry, this);
    }

    /**
     * Checks if a player is on this team.
     *
     * @param player the player to check
     * @return true if the player is on the team, false otherwise
     */
    public boolean isOnTeam(OfflinePlayer player) {
        return isOnTeam(player.getName());
    }

    /**
     * Checks if an entry is on this team.
     *
     * @param entry the entry to check
     * @return true if the entry is on the team, false otherwise
     */
    public boolean isOnTeam(String entry) {
        return TeamManager.INSTANCE.isOnTeam(entry, this);
    }

    /**
     * Sets the color of the team.
     *
     * @param color the color of the team
     * @return this team
     */
    public EventTeam setColor(ChatColor color) {
        this.color = color;
        return this;
    }

    /**
     * Sets the prefix of this team.
     *
     * @param prefix the prefix of this team
     * @return this team
     */
    public EventTeam setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * Sets the join setting override for this team.
     *
     * @param joinSetting the join setting for this team
     * @return this team
     */
    public EventTeam setJoinSettingOverride(JoinSetting joinSetting) {
        this.joinSettingOverride = joinSetting;
        return this;
    }

    /**
     * Sets the friendly fire setting override for this team.
     *
     * @param allowFriendlyFire the friendly fire setting for this team
     * @return this team
     */
    public EventTeam setFriendlyFireOverride(boolean allowFriendlyFire) {
        this.friendlyFireOverride = allowFriendlyFire;
        return this;
    }

    @Override
    public String getName() {
        return name;
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

    /**
     * Gets the join setting for this team or the default join setting if no override is set.
     *
     * @return the join setting for this team
     */
    public JoinSetting getJoinSetting() {
        return Objects.requireNonNullElseGet(
                joinSettingOverride,
                TeamManager.INSTANCE::getDefaultJoinSetting
        );
    }

    /**
     * Gets the friendly fire setting for this team or the default friendly fire setting if no override is set.
     *
     * @return the friendly fire for this team
     */
    public boolean isFriendlyFireAllowed() {
        return Objects.requireNonNullElseGet(
                friendlyFireOverride,
                TeamManager.INSTANCE::getDefaultAllowFriendlyFire
        );
    }

    /**
     * Creates a new team with the given name and registers it with the team manager.
     *
     * @param identifier the identifier of the team
     * @return the new team
     */
    public static EventTeam of(Identifier identifier) {
        return new EventTeam(identifier.toString());
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
