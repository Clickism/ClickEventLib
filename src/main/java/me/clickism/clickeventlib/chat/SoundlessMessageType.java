package me.clickism.clickeventlib.chat;

import org.bukkit.entity.Player;

/**
 * A message type that does not play a sound when sent.
 */
public class SoundlessMessageType extends MessageType {
    /**
     * Create a new soundless message type with the given prefix.
     *
     * @param prefix      the prefix of the message
     * @param titleFormat the format of the title message
     */
    public SoundlessMessageType(String prefix, String titleFormat) {
        super(prefix, titleFormat);
    }

    /**
     * Create a new soundless message type with the given prefix, title format and subtitle format.
     *
     * @param prefix         the prefix of the message
     * @param titleFormat    the format of the title message
     * @param subtitleFormat the format of the subtitle message
     */
    public SoundlessMessageType(String prefix, String titleFormat, String subtitleFormat) {
        super(prefix, titleFormat, subtitleFormat);
    }

    @Override
    public void playSound(Player player) {
        // Do nothing
    }
}
