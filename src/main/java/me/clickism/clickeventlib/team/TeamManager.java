package me.clickism.clickeventlib.team;

import me.clickism.clickeventlib.ClickEventLib;
import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages teams for events.
 */
public class TeamManager implements Listener {
    /**
     * The instance of the team manager.
     */
    public static final TeamManager INSTANCE = new TeamManager();

    private JoinSetting defaultJoinSetting = JoinSetting.EVERYONE_OPEN;
    private boolean defaultAllowFriendlyFire = false;

    private final NamedCollection<EventTeam> teams = new NamedCollection<>(new ArrayList<>());
    private final Map<UUID, Set<EventTeam>> inviteMap = new HashMap<>();

    /**
     * Creates a new team manager.
     */
    protected TeamManager() {
    }

    /**
     * Registers a team.
     *
     * @param eventTeam the team to register
     */
    void registerTeam(EventTeam eventTeam) {
        teams.addIfAbsent(eventTeam);
        tryRegisterTeamOnScoreboard(eventTeam);
    }

    /**
     * Tries to register the team on the main scoreboard if the main scoreboard is loaded.
     * If the main scoreboard is not loaded, the team will not be registered.
     *
     * @param eventTeam the team to register
     */
    public void tryRegisterTeamOnScoreboard(EventTeam eventTeam) {
        Scoreboard scoreboard = getMainScoreboard();
        if (scoreboard == null) return;
        Team team = scoreboard.getTeam(eventTeam.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(eventTeam.getName());
        }
        team.setColor(eventTeam.getColor());
        team.setAllowFriendlyFire(eventTeam.isFriendlyFireAllowed());
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
    }

    @EventHandler
    private void onWorldLoad(WorldLoadEvent event) {
        teams.forEach(this::tryRegisterTeamOnScoreboard);
    }

    /**
     * Gets the team of a player.
     *
     * @param player the player to get the team of
     * @return the team of the player, or null if the player is not on a team
     */
    @Nullable
    public EventTeam getTeamOf(OfflinePlayer player) {
        return getTeamOf(player.getName());
    }

    /**
     * Gets the color of a player's team.
     *
     * @param player   the player to get the color of
     * @param fallback the color to return if the player is not on a team
     * @return the color of the player's team, or the fallback color if the player is not on a team
     */
    public ChatColor getColorOf(OfflinePlayer player, ChatColor fallback) {
        EventTeam team = getTeamOf(player);
        if (team == null) return fallback;
        return team.getColor();
    }

    /**
     * Gets the team of an entry.
     *
     * @param entry the entry to get the team of
     * @return the team of the entry, or null if the entry is not on a team
     */
    @Nullable
    public EventTeam getTeamOf(String entry) {
        if (entry == null) return null;
        Scoreboard scoreboard = getMainScoreboard();
        if (scoreboard == null) return null;
        Team team = scoreboard.getEntryTeam(entry);
        if (team == null) return null;
        return teams.get(team.getName());
    }

    /**
     * Gets the entries of a team.
     *
     * @param eventTeam the team to get the entries of
     * @return the entries of the team
     */
    public Set<String> getEntries(EventTeam eventTeam) {
        Team scoreboardTeam = getScoreboardTeam(eventTeam);
        if (scoreboardTeam == null) throw new IllegalStateException("Team is not registered on the scoreboard");
        return scoreboardTeam.getEntries();
    }

    /**
     * Joins a player to a team.
     *
     * @param player    the player to team
     * @param eventTeam the team to team
     */
    public void joinTeam(OfflinePlayer player, EventTeam eventTeam) {
        joinTeam(player.getName(), eventTeam);
        refreshName(player);
    }

    /**
     * Joins an entry to a team.
     *
     * @param entry     the entry to team
     * @param eventTeam the team to team
     */
    public void joinTeam(String entry, EventTeam eventTeam) {
        if (entry == null) return;
        Team scoreboardTeam = getScoreboardTeam(eventTeam);
        if (scoreboardTeam == null) throw new IllegalStateException("Team is not registered on the scoreboard");
        scoreboardTeam.addEntry(entry);
    }

    /**
     * Leaves the team of a player.
     *
     * @param player the player to leave the team
     * @return the team that the player left, or null if the player was not on a team
     */
    public EventTeam leaveTeam(OfflinePlayer player) {
        EventTeam team = leaveTeam(player.getName());
        refreshName(player);
        return team;
    }

    /**
     * Leaves the team of an entry.
     *
     * @param entry the entry to leave
     * @return the team that the entry left, or null if the entry was not on a team
     */
    public EventTeam leaveTeam(String entry) {
        if (entry == null) return null;
        EventTeam eventTeam = getTeamOf(entry);
        if (eventTeam == null) return null;
        Team scoreboardTeam = getScoreboardTeam(eventTeam);
        if (scoreboardTeam == null) throw new IllegalStateException("Team is not registered on the scoreboard");
        scoreboardTeam.removeEntry(entry);
        return eventTeam;
    }

    /**
     * Leaves a player from a specific team.
     *
     * @param player the player to leave
     * @param team   the team to leave
     * @return true if the player was successfully left, false if the player was not on the team
     */
    public boolean leaveTeam(OfflinePlayer player, EventTeam team) {
        if (player == null) return false;
        if (!isOnTeam(player, team)) return false;
        leaveTeam(player);
        return true;
    }

    /**
     * Leaves an entry from a specific team.
     *
     * @param entry the entry to leave
     * @param team  the team to leave
     * @return true if the entry was successfully left, false if the entry was not on the team
     */
    public boolean leaveTeam(String entry, EventTeam team) {
        if (entry == null) return false;
        if (!isOnTeam(entry, team)) return false;
        leaveTeam(entry);
        return true;
    }

    private void refreshName(OfflinePlayer player) {
        ChatManager chatManager = ClickEventLib.INSTANCE.getChatManager();
        if (chatManager != null) {
            chatManager.refreshName(player);
        }
    }

    /**
     * Checks if a player is on a team.
     *
     * @param player    the player to check
     * @param eventTeam the team to check
     * @return true if the player is on the team, false otherwise
     */
    public boolean isOnTeam(OfflinePlayer player, EventTeam eventTeam) {
        return isOnTeam(player.getName(), eventTeam);
    }

    /**
     * Checks if an entry is on a team.
     *
     * @param entry     the entry to check
     * @param eventTeam the team to check
     * @return true if the entry is on the team, false otherwise
     */
    public boolean isOnTeam(String entry, EventTeam eventTeam) {
        if (entry == null) return false;
        EventTeam team = getTeamOf(entry);
        return team != null && team.equals(eventTeam);
    }

    /**
     * Checks if a player is invited to a team.
     *
     * @param player    the player to check
     * @param eventTeam the team to check
     * @return true if the player is invited to the team, false otherwise
     */
    public boolean isInvited(Player player, EventTeam eventTeam) {
        if (getEntries(eventTeam).isEmpty()) return true;
        return inviteMap.getOrDefault(player.getUniqueId(), Set.of()).contains(eventTeam);
    }

    /**
     * Invites a player to a team.
     *
     * @param player    the player to invite
     * @param eventTeam the team to invite the player to
     * @return true if the player was successfully invited, false if the player was already invited
     */
    public boolean invitePlayer(OfflinePlayer player, EventTeam eventTeam) {
        Set<EventTeam> teams = inviteMap.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (teams.contains(eventTeam)) return false;
        teams.add(eventTeam);
        return true;
    }

    /**
     * Checks if a given team can be invited to.
     *
     * @param eventTeam the team to check if it can be invited to
     * @return true if this team can be invited to, false otherwise
     */
    public boolean isInvitable(EventTeam eventTeam) {
        return eventTeam.getJoinSetting() == JoinSetting.EVERYONE_INVITE;
    }

    /**
     * Gets the prefix of a player's team.
     *
     * @param player the player to get the prefix of
     * @return the prefix of the player's team, or an empty string if the player is not on a team
     */
    public String getPrefix(OfflinePlayer player) {
        EventTeam team = getTeamOf(player);
        if (team == null) return "";
        return team.getPrefix();
    }

    /**
     * Sets the default team setting.
     *
     * @param defaultJoinSetting the default team setting
     */
    public void setDefaultJoinSetting(JoinSetting defaultJoinSetting) {
        this.defaultJoinSetting = defaultJoinSetting;
    }

    /**
     * Gets the default team setting.
     *
     * @return the default team setting
     */
    public JoinSetting getDefaultJoinSetting() {
        return defaultJoinSetting;
    }

    /**
     * Sets the default friendly fire setting.
     *
     * @param defaultAllowFriendlyFire the default friendly fire setting
     */
    public void setDefaultAllowFriendlyFire(boolean defaultAllowFriendlyFire) {
        this.defaultAllowFriendlyFire = defaultAllowFriendlyFire;
    }

    /**
     * Gets the default friendly fire setting.
     *
     * @return the default friendly fire setting
     */
    public boolean getDefaultAllowFriendlyFire() {
        return defaultAllowFriendlyFire;
    }

    /**
     * Gets the default team setting.
     *
     * @return the default team setting
     */
    public NamedCollection<EventTeam> getTeams() {
        return teams;
    }

    /**
     * Gets the scoreboard team of an event team.
     *
     * @param eventTeam the event team
     * @return the scoreboard team of the event team
     */
    @Nullable
    public Team getScoreboardTeam(EventTeam eventTeam) {
        Scoreboard scoreboard = getMainScoreboard();
        if (scoreboard == null) return null;
        return scoreboard.getTeam(eventTeam.getName());
    }

    @Nullable
    private Scoreboard getMainScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) return null;
        return scoreboardManager.getMainScoreboard();
    }
}
