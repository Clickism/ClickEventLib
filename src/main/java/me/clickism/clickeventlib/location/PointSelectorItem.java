package me.clickism.clickeventlib.location;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.item.CustomItem;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.clickeventlib.util.Identifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.Set;

class PointSelectorItem extends CustomItem {
    PointSelectorItem(String namespace, PointManager pointManager) {
        super(new Identifier(namespace, pointManager.getPointNamespace() + "_selector"),
                "&b👈 &lSelect Point: &f&l" + FormatUtils.formatNamespace(pointManager.getPointNamespace()),
                Material.IRON_AXE);
        addLore("&7&lRight-click &7on a block to add a point.",
                "&7This will not remove any existing points.",
                "&7Use &8&l/... remove &7 instead.");
        addEnchantmentGlint();
        hideAttributes();
        setAllowedActions(Set.of(Action.RIGHT_CLICK_BLOCK));
        setOnInteract(event -> {
            Block block = event.getClickedBlock();
            if (block == null) return;
            Player player = event.getPlayer();
            pointManager.addPoint(block.getLocation());
            MessageType.CONFIRM.send(player, "Point added at the clicked block.");
        });
    }
}
