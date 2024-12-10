package me.clickism.clickeventlib.phase;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.chat.Images;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Event boss bar.
 */
public class EventBar implements Listener {
    /**
     * Default title of the event bar.
     */
    public static final String DEFAULT_TITLE = Utils.colorize("&8< " + Images.LOGO_2LINE + " &8>");

    private final BossBar bar;

    /**
     * Create a new event boss bar.
     *
     * @param plugin plugin to register the boss bar with
     * @param title  title of the boss bar
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public EventBar(JavaPlugin plugin, String title) {
        this.bar = Bukkit.createBossBar(title, BarColor.BLUE, BarStyle.SOLID);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
        bar.setVisible(true);
    }

    @EventHandler(ignoreCancelled = true)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!bar.getPlayers().contains(player)) {
            bar.addPlayer(player);
        }
    }

    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        bar.removeAll();
    }

    /**
     * Set the color of the boss bar.
     *
     * @param color color to set
     */
    public void setColor(BarColor color) {
        bar.setColor(color);
    }

    /**
     * Set the progress of the boss bar.
     *
     * @param progress progress to set
     */
    public void setProgress(double progress) {
        bar.setProgress(progress);
    }

    /**
     * Set the style of the boss bar.
     *
     * @param style style to set
     */
    public void setStyle(BarStyle style) {
        bar.setStyle(style);
    }

    /**
     * Set the title of the boss bar.
     *
     * @param title title to set
     */
    public void setTitle(String title) {
        bar.setTitle(title);
    }

    /**
     * Set the visibility of the boss bar.
     *
     * @param visible visibility to set
     */
    public void setVisible(boolean visible) {
        bar.setVisible(visible);
    }

    /**
     * Check if the boss bar is visible.
     *
     * @return true if the boss bar is visible
     */
    public boolean isVisible() {
        return bar.isVisible();
    }
}
