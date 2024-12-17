package me.clickism.clickeventlib.property;

import me.clickism.subcommandapi.util.Named;

import java.util.function.Function;

/**
 * Represents a named property that stores a value of type T.
 *
 * @param <T> type of the property
 */
public abstract class Property<T> implements Named {
    private final String name;
    private T value;

    private final Function<String, T> parser;

    /**
     * Creates a new property with the given name and parser.
     *
     * @param name   the name of the property
     * @param parser the function to parse the string
     */
    public Property(String name, Function<String, T> parser) {
        this(name, null, parser);
    }

    /**
     * Creates a new property with the given name, default value, and parser.
     *
     * @param name   the name of the property
     * @param value  the value of the property
     * @param parser the function to parse the string
     */
    public Property(String name, T value, Function<String, T> parser) {
        this.name = name;
        this.value = value;
        this.parser = parser;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the property value.
     *
     * @return the property value
     */
    public T get() {
        return value;
    }

    /**
     * Set the property to the given value.
     *
     * @param t the value to set
     */
    public void set(T t) {
        this.value = t;
    }

    /**
     * Try to parse the given string and set the property to the parsed value.
     *
     * @param string the string to parse
     * @throws IllegalArgumentException if the string could not be parsed
     */
    public void parseAndSet(String string) throws IllegalArgumentException {
        try {
            set(parser.apply(string));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Failed to parse " + string + " for property " + name, exception);
        }
    }

    /**
     * Get the parser function for this property.
     *
     * @return the parser function
     */
    public Function<String, T> getParser() {
        return parser;
    }
}
