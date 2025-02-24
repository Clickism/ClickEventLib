package me.clickism.clickeventlib.annotations;

/**
 * Represents a registry that needs to be registered to Bukkit.
 */
public enum RegistryType {
    /**
     * Represents a Listener registration.
     */
    EVENT,
    /**
     * Represents a save on disable registration.
     */
    SAVE_ON_DISABLE,
    /**
     * Represents a save on interval registration.
     */
    SAVE_ON_INTERVAL
}
