package me.clickism.clickeventlib.phase;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Used for event handling that is specific to a phase.
 * Phase listeners will get registered/unregistered when a phase starts/ends.
 */
public abstract class PhaseListener implements Listener {

    /**
     * Creates a new phase listener.
     */
    public PhaseListener() {
    }

    /**
     * Register the event handler.
     *
     * @param plugin the plugin to register the event handler with
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Unregister the event handler.
     */
    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
