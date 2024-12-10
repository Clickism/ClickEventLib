package me.clickism.clickeventlib.property;

/**
 * Represents a debug property that stores a double value.
 */
public class DoubleProperty extends Property<Double> {
    /**
     * Creates a new double debug property with the given name and default value.
     *
     * @param name  the name of the property
     * @param value the default value of the property
     */
    public DoubleProperty(String name, Double value) {
        super(name, value, Double::parseDouble);
    }
}
