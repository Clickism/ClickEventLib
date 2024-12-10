package me.clickism.clickeventlib.particle;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * An animation that plays a particle effect in a line between two locations.
 */
public class LineAnimation extends ParticleAnimation {
    private final World world;

    private final Location from;
    private final Location to;

    private final ParticleConfiguration particle;

    private final long ticks;
    private final long period;

    private Runnable onFinish = () -> {};

    /**
     * Create a new line animation.
     *
     * @param from     from location
     * @param to       to location
     * @param particle particle configuration to use
     * @param ticks    number of ticks to animate over
     * @param period   period between each particle spawn in ticks
     */
    public LineAnimation(Location from, Location to, ParticleConfiguration particle, long ticks, long period) {
        this.world = Objects.requireNonNull(from.getWorld());
        if (!Objects.equals(world, to.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world.");
        }
        this.from = from;
        this.to = to;
        this.particle = particle;
        this.ticks = ticks;
        this.period = period;
    }

    @Override
    public LineAnimation setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    @Override
    public void play(JavaPlugin plugin) {
        final double deltaX = period * ((to.getX() - from.getX()) / ticks);
        final double deltaY = period * ((to.getY() - from.getY()) / ticks);
        final double deltaZ = period * ((to.getZ() - from.getZ()) / ticks);

        new BukkitRunnable() {
            private long currentTick = 0;
            
            @Override
            public void run() {
                if (currentTick > ticks) {
                    this.cancel();
                    onFinish.run();
                    return;
                }

                double x = from.getX() + deltaX * currentTick;
                double y = from.getY() + deltaY * currentTick;
                double z = from.getZ() + deltaZ * currentTick;

                Location currentLocation = new Location(world, x, y, z);
                world.spawnParticle(particle.particle(), currentLocation, particle.count(), particle.offset(), particle.offset(), particle.offset());

                currentTick++;
            }
        }.runTaskTimer(plugin, 0, period);
    }
}
