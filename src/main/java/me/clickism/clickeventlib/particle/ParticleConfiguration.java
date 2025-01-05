package me.clickism.clickeventlib.particle;

import org.bukkit.Particle;

/**
 * Represents a configuration for a particle effect.
 *
 * @param particle the particle type
 * @param offset   the offset of the particle
 * @param speed    the speed of the particle
 * @param count    the count of the particle
 */
public record ParticleConfiguration(Particle particle, double offset, double speed, int count) {
    /**
     * Creates a new particle configuration with the given particle type.
     *
     * @param particle the particle type
     * @return the particle configuration
     */
    public static ParticleConfiguration of(Particle particle) {
        return new ParticleConfiguration(particle, 0, 1, 1);
    }

    /**
     * Creates a new particle configuration with the given particle type and count.
     *
     * @param particle the particle type
     * @param offset   the offset of the particle
     * @param count    the count of the particle
     * @return the particle configuration
     */
    public static ParticleConfiguration of(Particle particle, double offset, int count) {
        return new ParticleConfiguration(particle, offset, 1, count);
    }

    /**
     * Creates a new particle configuration with the given particle type, offset, speed, and count.
     *
     * @param particle the particle type
     * @param offset   the offset of the particle
     * @param speed    the speed of the particle
     * @param count    the count of the particle
     * @return the particle configuration
     */
    public static ParticleConfiguration of(Particle particle, double offset, double speed, int count) {
        return new ParticleConfiguration(particle, offset, speed, count);
    }
}

