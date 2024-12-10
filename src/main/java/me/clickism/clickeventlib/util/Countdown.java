package me.clickism.clickeventlib.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A utility class that sends a countdown timer to players.
 */
public class Countdown {
    private final int seconds;
    private final long period;

    private Consumer<Integer> onTick = (s) -> {};
    private Runnable onFinish = () -> {};

    /**
     * Create a new countdown with a default real-time period of 20 ticks.
     *
     * @param seconds how many seconds to count down
     */
    public Countdown(int seconds) {
        this(seconds, 20);
    }

    /**
     * Create a new countdown.
     *
     * @param seconds how many seconds to count down
     * @param period  how many ticks between each second / countdown message
     */
    public Countdown(int seconds, long period) {
        this.seconds = seconds;
        this.period = period;
    }

    /**
     * Starts the countdown.
     *
     * @param plugin the plugin to run the countdown on
     * @return the task that is running the countdown
     */
    public BukkitTask start(JavaPlugin plugin) {
        return new BukkitRunnable() {
            private int remaining = Countdown.this.seconds;
            
            @Override
            public void run() {
                tick(this, remaining);
                remaining--;
            }
        }.runTaskTimer(plugin, 0, period);
    }

    /**
     * Set the callback that is called every tick.
     *
     * @param onTick callback that is called every tick with the remaining seconds in the countdown
     * @return this countdown
     */
    public Countdown setOnTick(@NotNull Consumer<Integer> onTick) {
        this.onTick = onTick;
        return this;
    }

    /**
     * Set the callback that is called when the countdown finishes.
     *
     * @param onFinish callback that is called when the countdown finishes
     * @return this countdown
     */
    public Countdown setOnFinish(@NotNull Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    private void tick(BukkitRunnable runnable, int seconds) {
        if (seconds == 0) {
            onFinish.run();
            runnable.cancel();
            return;
        }
        onTick.accept(seconds);
    }
}
