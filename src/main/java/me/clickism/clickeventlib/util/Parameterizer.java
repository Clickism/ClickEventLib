package me.clickism.clickeventlib.util;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for parameterizing strings with placeholders.
 * Placeholders are defined in the format <code>{key}</code> and can be replaced
 * with corresponding values provided in a map.
 */
public class Parameterizer {
    private static final String FORMAT = "{%s}";

    private final Map<String, Object> params = new HashMap<>();

    /**
     * Creates a new parameterizer.
     */
    public Parameterizer() {
    }

    /**
     * Defines a value for a placeholder.
     *
     * @param key   placeholder key, without brackets
     * @param value value to replace the placeholder with
     * @return this Parameterizer
     */
    public Parameterizer put(String key, @NonNull Object value) {
        this.params.put(key, value);
        return this;
    }

    /**
     * Defines multiple values for placeholders.
     *
     * @param map map of placeholder keys and values
     * @return this Parameterizer
     */
    public Parameterizer putAll(Map<String, Object> map) {
        this.params.putAll(map);
        return this;
    }

    /**
     * Applies the parameterized values to the string.
     *
     * @param string string with placeholders
     * @return the parameterized string
     */
    public String apply(String string) {
        String result = string;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = String.format(FORMAT, entry.getKey());
            result = result.replace(placeholder, entry.getValue().toString());
        }
        return result;
    }

    /**
     * Applies the parameterized values to the string and colorizes it.
     *
     * @param string string with placeholders
     * @return the colorized parameterized string
     * @see Utils#colorize(String)
     */
    public String applyColorized(String string) {
        return Utils.colorize(apply(string));
    }
}
