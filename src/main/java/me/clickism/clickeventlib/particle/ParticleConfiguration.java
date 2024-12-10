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
}
