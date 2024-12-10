package me.clickism.clickeventlib.property;

/**
 * Represents a debug property that stores a boolean value.
 */
public class BooleanProperty extends Property<Boolean> {
    /**
     * Creates a new boolean debug property with the given name and default value.
     *
     * @param name         the name of the property
     * @param defaultValue the default value of the property
     */
    public BooleanProperty(String name, boolean defaultValue) {
        super(name, defaultValue, Boolean::parseBoolean);
    }
}
