package me.clickism.clickeventlib.command.argument;

/**
 * Represents an argument that is a double.
 */
public class DoubleArgument extends ValueArgument<Double> {
    /**
     * Create a new double argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public DoubleArgument(String name, boolean required) {
        super(name, required, Double::parseDouble);
    }
}
