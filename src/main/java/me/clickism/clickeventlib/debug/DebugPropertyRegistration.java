package me.clickism.clickeventlib.debug;

import me.clickism.clickeventlib.property.Property;
import me.clickism.subcommandapi.util.NamedCollection;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Manages the registration of debug properties.
 */
public class DebugPropertyRegistration {
    /**
     * No constructor for static class.
     */
    private DebugPropertyRegistration() {
    }

    private static final NamedCollection<Property<?>> debugProperties = new NamedCollection<>(new ArrayList<>());

    /**
     * Register a new debug property. The registered property can be accessed and modified in-game.
     *
     * @param debugProperty the debug property to register
     * @param <T>           the type of the debug property
     * @return the registered debug property
     */
    public static <T> Property<T> register(Property<T> debugProperty) {
        debugProperties.add(debugProperty);
        return debugProperty;
    }

    /**
     * Get a debug property by name.
     *
     * @param name the name of the debug property
     * @return the debug property with the given name, or empty if not found
     */
    public static Optional<Property<?>> getDebugProperty(String name) {
        return Optional.ofNullable(debugProperties.get(name));
    }

    /**
     * Get all registered debug properties.
     *
     * @return all registered debug properties
     */
    public static NamedCollection<Property<?>> getDebugProperties() {
        return debugProperties;
    }
}
