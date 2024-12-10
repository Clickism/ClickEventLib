package me.clickism.clickeventlib.particle;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a particle animation.
 * Extending classes should run the {@link #onFinish} runnable when the animation finishes.
 */
public abstract class ParticleAnimation {
    /**
     * Constructs a new particle animation.
     */
    public ParticleAnimation() {
    }

    /**
     * The runnable to run when the animation finishes.
     */
    protected Runnable onFinish = () -> {};

    /**
     * Plays the animation.
     *
     * @param plugin plugin
     */
    abstract void play(JavaPlugin plugin);

    /**
     * Sets the runnable to run when the animation finishes.
     *
     * @param onFinish the runnable to run when the animation finishes
     * @return this
     */
    public ParticleAnimation setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }
}
