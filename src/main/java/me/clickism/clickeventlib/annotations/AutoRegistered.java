package me.clickism.clickeventlib.annotations;

import java.lang.annotation.Documented;

/**
 * Annotation to mark constructors or methods that automatically register
 * their components (such as Listeners, Commands, etc.) to the Bukkit API.
 */
@Documented
public @interface AutoRegistered {
    /**
     * The type of registry that will automatically be registered to.
     *
     * @return the type of registry
     */
    RegistryType[] type();
}

