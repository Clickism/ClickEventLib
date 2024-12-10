package me.clickism.clickeventlib.particle;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * An engine for playing particle animations.
 */
public class ParticleEngine {

    /**
     * The plugin that this particle engine is for.
     */
    protected final JavaPlugin plugin;

    /**
     * Create a new particle engine for the given plugin.
     *
     * @param plugin the plugin
     */
    public ParticleEngine(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Plays the given particle animation.
     *
     * @param animation the particle animation to play
     */
    public void playAnimation(ParticleAnimation animation) {
        animation.play(plugin);
    }
}
