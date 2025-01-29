package me.clickism.clickeventlib.trigger;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.item.CustomItem;
import me.clickism.clickeventlib.util.Identifier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

/**
 * Represents a trigger interaction selector item.
 */
public class TriggerInteractionSelectorItem extends CustomItem {
    private final TriggerManager triggerManager;
    private final TriggerSelectionManager selectionManager;

    private static final String ITEM_ID = "trigger_interaction_selector";
    private static final String DISPLAY_NAME = "&3&lTrigger Interaction Selector";
    private static final List<String> LORE = List.of(
            "&bRight-click to select an &linteraction location.",
            "&bUse &3&l/... trigger cancel &bto stop selecting."
    );

    /**
     * Creates a new trigger interaction selector item.
     *
     * @param namespace        namespace of the plugin
     * @param triggerManager   trigger manager
     * @param selectionManager trigger selection manager
     */
    public TriggerInteractionSelectorItem(String namespace, TriggerManager triggerManager, TriggerSelectionManager selectionManager) {
        super(new Identifier(namespace, ITEM_ID), DISPLAY_NAME, Material.DIAMOND_AXE);
        setLore(LORE);
        addEnchantmentGlint();
        hideAttributes();
        this.triggerManager = triggerManager;
        this.selectionManager = selectionManager;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        event.setCancelled(true);
        Location location = block.getLocation();
        Player player = event.getPlayer();
        if (!selectionManager.isSelectingInteraction(player)) {
            MessageType.FAIL.send(player, "You are not currently selecting a trigger interaction.");
            return;
        }
        selectPosition(player, location);
    }

    /**
     * Selects the given location for the trigger box the player is currently selecting.
     */
    private void selectPosition(Player player, Location location) {
        selectionManager.selectInteraction(player, location, triggerManager);
        MessageType.CONFIRM.send(player, "Trigger interaction created.");
    }
}
