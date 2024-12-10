package me.clickism.clickeventlib.location;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.item.CustomItem;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.clickeventlib.util.Identifier;
import org.bukkit.Material;
import org.bukkit.entity.Player;

class PointAdderItem extends CustomItem {
    PointAdderItem(String namespace, PointManager pointManager) {
        super(new Identifier(namespace, pointManager.getPointNamespace() + "_adder"), 
                "&e↓ &lAdd Here: &f&l" + FormatUtils.formatNamespace(pointManager.getPointNamespace()), 
                Material.COMPASS);
        addLore("&7Click to add a point at your current location.",
                "&7This will not remove any existing points.",
                "&7Use &8&l/... remove &7 instead.");
        addEnchantmentGlint();
        hideAttributes();
        setOnInteract(event -> {
            Player player = event.getPlayer();
            pointManager.addPoint(player.getLocation());
            MessageType.CONFIRM.send(player, "Point added at your current location.");
        });
    }
}
