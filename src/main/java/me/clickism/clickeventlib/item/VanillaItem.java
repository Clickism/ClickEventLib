package me.clickism.clickeventlib.item;

import me.clickism.clickeventlib.util.Identifier;
import org.bukkit.Material;

/**
 * Represents a vanilla custom item. Vanilla custom items cannot be registered, or used for interactions.
 * They are simply an easy way to modify default vanilla items, by adding enchantments, etc.
 */
public class VanillaItem extends CustomItem {
    /**
     * Vanilla item identifier.
     */
    final static Identifier VANILLA_IDENTIFIER = new Identifier("minecraft", "item");

    private VanillaItem(Material material) {
        super(VANILLA_IDENTIFIER, material);
    }

    /**
     * Creates a new vanilla custom item. Attempting to register a vanilla custom item will throw an exception.
     *
     * @param material the material of the item
     * @return the vanilla custom item
     */
    public static VanillaItem of(Material material) {
        return new VanillaItem(material);
    }
}
