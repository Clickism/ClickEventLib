package me.clickism.clickeventlib.util;

import me.clickism.clickeventlib.chat.Images;
import me.clickism.clickeventlib.chat.MessageType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Utility methods.
 */
public class Utils {
    /**
     * No constructor for static class
     */
    private Utils() {
    }

    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    /**
     * Broadcasts a message to all online OP players and logs the message in the console.
     *
     * @param message the message to broadcast
     * @param level   the log level
     */
    public static void broadcastToOpsAndLog(String message, Level level) {
        Bukkit.getLogger().log(level, message);
        broadcastToOps(message, level);
    }

    /**
     * Broadcasts a message to all online OP players and logs the message in the console.
     *
     * @param message   the message to broadcast
     * @param level     the log level
     * @param throwable the throwable to log
     */
    public static void broadcastToOpsAndLog(String message, Level level, Throwable throwable) {
        Bukkit.getLogger().log(level, message, throwable);
        broadcastToOps(message, level);
    }

    /**
     * Broadcasts a message to all online OP players.
     *
     * @param message the message to broadcast
     * @param level   the log level
     */
    private static void broadcastToOps(String message, Level level) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp()) continue;
            if (level.equals(Level.WARNING) || level.equals(Level.INFO)) {
                MessageType.WARN.send(player, message);
            } else if (level.equals(Level.SEVERE)) {
                MessageType.FAIL.send(player, message);
            } else {
                player.sendMessage(colorize(message));
            }
        }
    }

    /**
     * a green | b aqua | c red | d pink | e yellow | f white
     * <p>
     * 0 black | 1 dark blue | 2 dark green | 3 dark aqua | 4 dark red | 5 dark purple | 6 gold | 7 gray | 8 dark gray | 9 blue
     *
     * @param text the text to colorize
     * @return colorized string
     **/
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Checks if an entity damage event is fatal.
     *
     * @param event the event to check
     * @return true if the event is fatal, false otherwise
     */
    public static boolean isFatal(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return false;
        return event.getFinalDamage() >= livingEntity.getHealth();
    }

    /**
     * Clears the player's inventory.
     *
     * @param player the player whose inventory to clear
     */
    public static void clearInventory(Player player) {
        player.setItemOnCursor(null);
        InventoryView view = player.getOpenInventory();
        view.getTopInventory().clear();
        view.getBottomInventory().clear();
    }

    /**
     * Resets a player's health, food, and inventory, etc. and sets their game mode to survival.
     *
     * @param player the player to reset
     */
    public static void resetPlayer(Player player) {
        resetPlayer(player, GameMode.SURVIVAL);
    }

    /**
     * Resets a player's health, food, and inventory, etc. and sets their game mode to the given game mode.
     *
     * @param player   the player to reset
     * @param gameMode the game mode to set the player to
     */
    public static void resetPlayer(Player player, GameMode gameMode) {
        Utils.clearInventory(player);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(4);
        player.setFireTicks(0);
        player.setFreezeTicks(0);
        player.setExp(0);
        player.setTotalExperience(0);
        player.setGlowing(false);
        player.setInvulnerable(false);
        player.setInvisible(false);
        player.setGameMode(gameMode);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    /**
     * Parses an integer from a string, returning null if the string is not a valid integer.
     *
     * @param string the string to parse
     * @return the integer, or null if the string is not a valid integer
     */
    public static Integer parseIntOrNull(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Adds an item to a player's inventory, dropping the item on the ground if the inventory is full.
     *
     * @param player the player to add the item to
     * @param item   the item to add
     */
    public static void addItem(Player player, ItemStack item) {
        player.getInventory().addItem(item.clone()).values().forEach(drop ->
                player.getWorld().dropItem(player.getLocation(), drop));
    }

    /**
     * Adds an item to a player's inventory if they do not already have the item.
     *
     * @param player the player to add the item to
     * @param item   the item to add
     */
    public static void addItemIfNotHas(Player player, ItemStack item) {
        if (!hasItem(player, item)) {
            addItem(player, item);
        }
    }

    /**
     * Removes a number of items from a player's inventory.
     *
     * @param player the player to remove the item from
     * @param item   the item to remove
     * @param amount the amount to remove
     */
    public static void removeItem(Player player, ItemStack item, int amount) {
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        player.getInventory().removeItem(clone);
    }

    /**
     * Removes all instances of an item from a player's inventory.
     *
     * @param player the player to remove the item from
     * @param item   the item to remove
     */
    public static void removeItem(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        Arrays.stream(inv.getContents())
                .filter(i -> i != null && i.isSimilar(item))
                .forEach(inv::removeItem);
    }

    /**
     * Checks if a player has an item in their inventory.
     *
     * @param player the player to check
     * @param item   the item to check for
     * @return true if the player has the item, false otherwise
     */
    public static boolean hasItem(Player player, ItemStack item) {
        return player.getInventory().containsAtLeast(item, 1);
    }

    /**
     * Teleports a player smoothly to a location.
     *
     * @param player   the player to teleport
     * @param location the location to teleport to
     * @param plugin   the plugin to run the task on
     */
    public static void teleportSmoothly(Player player, Location location, Plugin plugin) {
        teleportSmoothly(player, location, plugin, () -> {});
    }

    /**
     * Teleports a player smoothly to a location and runs a task after teleporting.
     *
     * @param player     the player to teleport
     * @param location   the location to teleport to
     * @param plugin     the plugin to run the task on
     * @param onTeleport the task to run after teleporting
     */
    public static void teleportSmoothly(Player player, Location location, Plugin plugin, Runnable onTeleport) {
        fade(player, 6, 2, 6);
        SCHEDULER.runTaskLater(plugin, () -> {
            player.teleport(location);
            onTeleport.run();
        }, 6);
    }

    /**
     * Teleports a collection of players smoothly to a location.
     *
     * @param players  the players to teleport
     * @param location the location to teleport to
     * @param plugin   the plugin to run the task on
     */
    public static void teleportSmoothly(Collection<? extends Player> players, Location location, Plugin plugin) {
        teleportSmoothly(players, location, plugin, player -> {});
    }


    /**
     * Teleports a player smoothly to a location and runs a task after teleporting.
     *
     * @param players    the players to teleport
     * @param location   the location to teleport to
     * @param plugin     the plugin to run the task on
     * @param onTeleport the task to run after teleporting
     */
    public static void teleportSmoothly(Collection<? extends Player> players, Location location, Plugin plugin, Consumer<Player> onTeleport) {
        players.forEach(player -> fade(player, 6, 2, 6));
        SCHEDULER.runTaskLater(plugin, task -> players.forEach(player -> {
            player.teleport(location);
            onTeleport.accept(player);
        }), 6);
    }

    /**
     * Sends a simple "You can't do that" message to a player.
     *
     * @param player the player to send the message to
     */
    public static void fail(Player player) {
        sendActionbar(player, "&8< &cYou can't do that &8>");
        MessageType.FAIL.playSound(player);
    }

    /**
     * Sends a colorized action bar message to a player.
     *
     * @param player  the player to send the message to
     * @param message the message to send
     */
    public static void sendActionbar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorize(message)));
    }

    /**
     * Sends a colorized title to a player.
     *
     * @param player  the player to send the title to
     * @param fadeIn  the time in ticks for the title to fade in
     * @param stay    the time in ticks for the title to stay
     * @param fadeOut the time in ticks for the title to fade out
     */
    public static void fade(Player player, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(Images.FADE + "", "", fadeIn, stay, fadeOut);
    }

    /**
     * Sends a standard colorized title to a player, it will stay for 3 seconds.
     *
     * @param player   the player to send the title to
     * @param title    the title to send
     * @param subtitle the subtitle to send
     */
    public static void title(Player player, String title, String subtitle) {
        player.sendTitle(Utils.colorize(title), Utils.colorize(subtitle), 5, 60, 10);
    }

    /**
     * Sends a colorized title to a player that will show instantly and fade out in 10 ticks.
     *
     * @param player   the player to send the title to
     * @param title    the title to send
     * @param subtitle the subtitle to send
     * @param stay     the time in ticks for the title to stay
     */
    public static void titleInstant(Player player, String title, String subtitle, int stay) {
        player.sendTitle(Utils.colorize(title), Utils.colorize(subtitle), 0, stay, 10);
    }

    /**
     * Sends a colorized title to a player.
     *
     * @param player   the player to send the title to
     * @param title    the title to send
     * @param subtitle the subtitle to send
     * @param fadeIn   the time in ticks for the title to fade in
     * @param stay     the time in ticks for the title to stay
     * @param fadeOut  the time in ticks for the title to fade out
     */
    public static void title(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(Utils.colorize(title), Utils.colorize(subtitle), fadeIn, stay, fadeOut);
    }
}
