package me.clickism.clickeventlib.util;

import org.bukkit.NamespacedKey;

/**
 * An identifier with a namespace and key.
 *
 * @param namespace namespace
 * @param key       key
 */
public record Identifier(String namespace, String key) {
    @Override
    public String toString() {
        return namespace + ":" + key;
    }

    /**
     * Convert this identifier to a bukkit namespaced key.
     *
     * @return namespaced key
     */
    public NamespacedKey toNamespacedKey() {
        return NamespacedKey.fromString(toString());
    }

    /**
     * Convert a string to an identifier.
     *
     * @param string string
     * @return identifier
     * @throws IllegalArgumentException if the string is invalid
     */
    public static Identifier fromString(String string) throws IllegalArgumentException {
        String[] parts = string.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid identifier string: " + string);
        }
        return new Identifier(parts[0], parts[1]);
    }
}
