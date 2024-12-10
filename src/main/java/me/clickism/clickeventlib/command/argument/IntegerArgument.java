package me.clickism.clickeventlib.command.argument;

/**
 * Represents an argument that is an integer.
 */
public class IntegerArgument extends ValueArgument<Integer> {
    /**
     * Create a new integer argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public IntegerArgument(String name, boolean required) {
        super(name, required, Integer::parseInt);
    }
}
