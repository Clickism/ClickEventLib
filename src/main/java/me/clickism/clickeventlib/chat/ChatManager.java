package me.clickism.clickeventlib.chat;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.team.RoleManager;
import me.clickism.clickeventlib.team.TeamManager;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

/**
 * Manages chat permissions and formatting.
 */
public class ChatManager implements Listener {
    /**
     * Represents the type of chat.
     */
    public enum ChatType {
        /**
         * Voice chat.
         */
        VOICE,
        /**
         * Text chat.
         */
        TEXT
    }

    /**
     * The format of the chat message:
     * Team prefix, role prefix, player name, message
     */
    private static final String CHAT_FORMAT = "%s%s" + GRAY + "%s: " + RESET + "%s";

    private static final String JOIN_FORMAT = DARK_GREEN + "<" + GREEN + "↓" + DARK_GREEN + "> " + GRAY + "%s";
    private static final String QUIT_FORMAT = DARK_RED + "<" + RED + "↑" + DARK_RED + "> " + GRAY + STRIKETHROUGH + "%s";

    private static final String DISPLAY_NAME_FORMAT = "%s%s" + GRAY + "%s";

    private final JavaPlugin plugin;

    private final RoleManager roleManager;
    private final TeamManager teamManager;

    /**
     * Create a new chat manager with the given plugin.
     *
     * @param plugin      the plugin to create the chat manager for
     * @param roleManager the role manager
     * @param teamManager the team manager
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public ChatManager(JavaPlugin plugin, RoleManager roleManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.roleManager = roleManager;
        this.teamManager = teamManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);
        if (!player.hasPermission("clickeventlib.chat")) {
            MessageType.FAIL.send(player, "You are not allowed to use the chat.");
            return;
        }
        String message = event.getMessage();
        if (player.isOp()) {
            message = Utils.colorize(message);
        }
        String teamPrefix = teamManager.getPrefix(player);
        String rolePrefix = roleManager.getPrefix(player.getUniqueId());
        String playerName = player.getName();
        String formattedMessage = String.format(CHAT_FORMAT, teamPrefix, rolePrefix, playerName, message);
        Bukkit.broadcastMessage(formattedMessage);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        refreshName(player);
        String playerName = player.getName();
        // ChatColor color = teamManager.getColorOf(player, GRAY);
        String message = String.format(JOIN_FORMAT, playerName);
        event.setJoinMessage(message);
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        // ChatColor color = teamManager.getColorOf(player, GRAY);
        String message = String.format(QUIT_FORMAT, playerName);
        event.setQuitMessage(message);
    }

    @EventHandler
    private void onSignEdit(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.isOp()) return;
        String[] lines = event.getLines();
        for (int i = 0; i < lines.length; i++) {
            event.setLine(i, Utils.colorize(lines[i]));
        }
    }

    /**
     * Refreshes the name of an offline player if they are online.
     *
     * @param player the player to refresh the name of
     */
    public void refreshName(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) return;
        refreshName(onlinePlayer);
    }

    /**
     * Refreshes the name of a player.
     *
     * @param player the player to refresh the name of
     */
    public void refreshName(Player player) {
        UUID uuid = player.getUniqueId();
        String teamPrefix = teamManager.getPrefix(player);
        String rolePrefix = roleManager.getPrefix(uuid);
        String playerName = player.getName();
        String displayName = String.format(DISPLAY_NAME_FORMAT, teamPrefix, rolePrefix, playerName);
        player.setPlayerListName(displayName);
    }

    /**
     * Mute a player's chat.
     *
     * @param player   the player to mute
     * @param chatType the type of chat to mute
     */
    public void mute(Player player, ChatType chatType) {
        switch (chatType) {
            case VOICE -> muteVoiceChat(player);
            case TEXT -> muteTextChat(player);
        }
    }

    /**
     * Unmute a player's chat.
     *
     * @param player   the player to unmute
     * @param chatType the type of chat to unmute
     */
    public void unmute(Player player, ChatType chatType) {
        switch (chatType) {
            case VOICE -> unmuteVoiceChat(player);
            case TEXT -> unmuteTextChat(player);
        }
    }

    private void muteTextChat(Player player) {
        if (player.isOp()) return;
        player.addAttachment(plugin).setPermission("clickeventlib.chat", false);
    }

    private void unmuteTextChat(Player player) {
        player.addAttachment(plugin).setPermission("clickeventlib.chat", true);
    }

    private void muteVoiceChat(Player player) {
        if (player.isOp()) return;
        player.addAttachment(plugin).setPermission("voicechat.speak", false);
    }

    private void unmuteVoiceChat(Player player) {
        player.addAttachment(plugin).setPermission("voicechat.speak", true);
    }
}
