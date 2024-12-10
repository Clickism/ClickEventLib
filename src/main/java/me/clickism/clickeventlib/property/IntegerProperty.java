package me.clickism.clickeventlib.property;

/**
 * Represents a debug property that stores an integer value.
 */
public class IntegerProperty extends Property<Integer> {
    /**
     * Creates a new integer debug property with the given name and default value.
     *
     * @param name    the name of the property
     * @param integer the default value of the property
     */
    public IntegerProperty(String name, Integer integer) {
        super(name, integer, Integer::parseInt);
    }
}
