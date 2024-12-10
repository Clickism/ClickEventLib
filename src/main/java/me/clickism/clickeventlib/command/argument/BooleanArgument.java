package me.clickism.clickeventlib.command.argument;

import java.util.List;

/**
 * Represents an argument that is a boolean.
 */
public class BooleanArgument extends ValueArgument<Boolean> {
    private static final List<String> BOOLEAN_VALUES = List.of("true", "false");

    /**
     * Create a new boolean argument with the given name and required status.
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public BooleanArgument(String name, boolean required) {
        super(name, required, Boolean::parseBoolean, BOOLEAN_VALUES);
    }
}
