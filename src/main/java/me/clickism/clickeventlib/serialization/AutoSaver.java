package me.clickism.clickeventlib.serialization;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Automatically saves objects on plugin disable.
 */
public class AutoSaver implements Listener {
    /**
     * Save interval unit in seconds.
     */
    public static final long INTERVAL_UNIT = 10;

    private final List<AutoSaved> saveOnDisableList = new ArrayList<>();
    private final Map<AutoSaved, Integer> saveOnIntervalList = new HashMap<>();

    private final JavaPlugin plugin;

    private long currentInterval = 0;

    /**
     * Create a new auto saver.
     * <p>
     * The auto saver must be registered via {@link #register()}.
     *
     * @param plugin plugin
     */
    public AutoSaver(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register the auto saver.
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        timer();
    }

    private void timer() {
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            currentInterval++;
            saveOnIntervalList.forEach((toSave, interval) -> {
                if (interval < INTERVAL_UNIT || currentInterval % (interval / INTERVAL_UNIT) != 0) return;
                toSave.save();
            });
        }, 20L * INTERVAL_UNIT, 20L * INTERVAL_UNIT);
    }

    /**
     * Register a {@link AutoSaved} object to be saved on plugin disable.
     *
     * @param toSave object to save
     */
    public void registerSaveOnDisable(AutoSaved toSave) {
        saveOnDisableList.add(toSave);
    }

    /**
     * Register a {@link AutoSaved} object to be saved on an interval.
     * If the interval is less than {@link #INTERVAL_UNIT}, it will be saved on every save cycle.
     * If the interval is not a direct multiple of {@link #INTERVAL_UNIT}, it will be saved on the next possible cycle.
     * <p>
     * i.E: 5 seconds: will be saved every 10 seconds.
     * i.E: 25 seconds: will first be saved after 30 seconds, then after 20 seconds... and so on.
     *
     * @param toSave   object to save
     * @param interval interval in seconds
     */
    public void registerSaveOnInterval(AutoSaved toSave, int interval) {
        saveOnIntervalList.put(toSave, interval);
    }

    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        if (!event.getPlugin().equals(plugin)) return;
        saveOnDisableList.forEach(AutoSaved::save);
    }
}
