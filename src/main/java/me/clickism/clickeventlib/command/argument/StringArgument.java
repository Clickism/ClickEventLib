package me.clickism.clickeventlib.command.argument;

import java.util.function.Function;

/**
 * Represents an argument that is a string.
 */
public class StringArgument extends ValueArgument<String> {
    /**
     * Create a new string argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public StringArgument(String name, boolean required) {
        super(name, required, Function.identity());
    }
}
