package me.clickism.clickeventlib.trigger;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.item.CustomItem;
import me.clickism.clickeventlib.util.Identifier;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;

/**
 * Represents a trigger box selector item.
 */
public class TriggerBoxSelectorItem extends CustomItem {
    private static final Set<Action> ALLOWED_ACTIONS = Set.of(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK);

    private final TriggerManager triggerManager;
    private final TriggerSelectionManager selectionManager;

    private static final String ITEM_ID = "trigger_box_selector";
    private static final String DISPLAY_NAME = "&6&lTrigger Box Selector";
    private static final List<String> LORE = List.of(
            "&eLeft-click to select &lpos1.",
            "&eRight-click to select &lpos2.",
            "&eUse &6&l/... trigger cancel &eto stop selecting."
    );

    /**
     * Creates a new trigger box selector item.
     *
     * @param namespace        namespace of the plugin
     * @param triggerManager   trigger manager
     * @param selectionManager trigger selection manager
     */
    public TriggerBoxSelectorItem(String namespace, TriggerManager triggerManager, TriggerSelectionManager selectionManager) {
        super(new Identifier(namespace, ITEM_ID), DISPLAY_NAME, Material.GOLDEN_AXE);
        addLore(LORE);
        addEnchantmentGlint();
        hideAttributes();
        setAllowedActions(ALLOWED_ACTIONS);
        this.triggerManager = triggerManager;
        this.selectionManager = selectionManager;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        event.setCancelled(true);
        Location loc = block.getLocation();
        Player player = event.getPlayer();
        if (!selectionManager.isSelectingBox(player)) {
            MessageType.FAIL.send(player, "You are not currently selecting a trigger box.");
            return;
        }
        Action action = event.getAction();
        selectPosition(player, loc, action);
    }

    /**
     * Selects the given location for the trigger box the player is currently selecting.
     */
    private void selectPosition(Player player, Location location, Action action) {
        TriggerBox box = switch (action) {
            case LEFT_CLICK_BLOCK -> selectionManager.selectPosAndBuild(player, location,
                    TriggerSelectionManager.PosType.POS1, triggerManager);
            case RIGHT_CLICK_BLOCK -> selectionManager.selectPosAndBuild(player, location,
                    TriggerSelectionManager.PosType.POS2, triggerManager);
            default -> null;
        };
        switch (action) {
            case LEFT_CLICK_BLOCK -> MessageType.CONFIRM.send(player, "Selected &lpos1.");
            case RIGHT_CLICK_BLOCK -> MessageType.CONFIRM.send(player, "Selected &lpos2.");
        }
        if (box == null) return;
        String name = box.getName();
        MessageType.CONFIRM.send(player, "Trigger created: &l" + name);
    }
}
