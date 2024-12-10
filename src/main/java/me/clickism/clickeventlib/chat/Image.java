package me.clickism.clickeventlib.chat;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents an image that can be displayed in chat/text.
 */
public class Image {
    private final static ChatColor NO_SHADOW_COLOR = ChatColor.of("#4e5c24");

    private final String key;

    /**
     * Create a new image with the given key.
     *
     * @param key the key of the image
     */
    public Image(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return NO_SHADOW_COLOR + key;
    }
}
