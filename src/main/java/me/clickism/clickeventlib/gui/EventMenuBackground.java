package me.clickism.clickeventlib.gui;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickgui.menu.Button;
import me.clickism.clickgui.menu.MenuBackground;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Default black background for an event menu.
 */
public class EventMenuBackground implements MenuBackground {
    private static final Button DARK_BUTTON = Button.withIcon(Material.BLACK_STAINED_GLASS_PANE)
            .setOnClick((player, view, slot) -> MessageType.FAIL.playSound(player));
    private static final Button LIGHT_BUTTON = Button.withIcon(Material.GRAY_STAINED_GLASS_PANE)
            .setOnClick((player, view, slot) -> MessageType.FAIL.playSound(player));

    /**
     * Creates a new event menu background.
     */
    public EventMenuBackground() {
    }

    @Override
    public @Nullable Button getButton(int i) {
        return (i / 9) % 2 == 0 ? DARK_BUTTON : LIGHT_BUTTON;
    }
}
