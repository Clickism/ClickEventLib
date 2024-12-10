package me.clickism.clickeventlib.command.argument;

/**
 * A string argument that replaces underscores with spaces.
 */
public class TextArgument extends ValueArgument<String> {
    /**
     * Create a new string argument with the given name and required status.
     * <p>
     * <b>This argument will replace underscores with spaces.</b>
     *
     * @param name     the name of the argument
     * @param required whether the argument is required
     */
    public TextArgument(String name, boolean required) {
        super(name, required, (arg) -> arg.replace('_', ' '));
    }
}
