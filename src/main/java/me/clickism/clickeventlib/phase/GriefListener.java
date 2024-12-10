package me.clickism.clickeventlib.phase;

import me.clickism.clickeventlib.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * A listener that cancels griefing actions based on the settings provided.
 */
public class GriefListener extends PhaseListener {

    private final boolean canBreakBlocks;
    private final boolean canPlaceBlocks;
    private final boolean canDropItems;
    private final boolean canPickupItems;
    private final boolean canOpenContainers;
    private final boolean canInteractWithTrapdoors;
    private final boolean canInteractWithFenceGates;
    private final boolean canEditSigns;
    private final boolean canInteractWithItemFrames;
    private final boolean canLoseHunger;

    /**
     * Create a new grief settings object.
     *
     * @param canBreakBlocks            whether players can break blocks
     * @param canPlaceBlocks            whether players can place blocks
     * @param canDropItems              whether players can drop items
     * @param canPickupItems            whether players can pick up items
     * @param canOpenContainers         whether players can open containers
     * @param canInteractWithTrapdoors  whether players can interact with trapdoors
     * @param canInteractWithFenceGates whether players can interact with fence gates
     * @param canEditSigns              whether players can edit signs
     * @param canInteractWithItemFrames whether players can interact with item frames
     * @param canLoseHunger             whether players can lose hunger
     */
    public GriefListener(boolean canBreakBlocks, boolean canPlaceBlocks, boolean canDropItems, boolean canPickupItems,
                         boolean canOpenContainers, boolean canInteractWithTrapdoors, boolean canInteractWithFenceGates,
                         boolean canEditSigns, boolean canInteractWithItemFrames, boolean canLoseHunger) {
        this.canBreakBlocks = canBreakBlocks;
        this.canPlaceBlocks = canPlaceBlocks;
        this.canDropItems = canDropItems;
        this.canPickupItems = canPickupItems;
        this.canOpenContainers = canOpenContainers;
        this.canInteractWithTrapdoors = canInteractWithTrapdoors;
        this.canInteractWithFenceGates = canInteractWithFenceGates;
        this.canEditSigns = canEditSigns;
        this.canInteractWithItemFrames = canInteractWithItemFrames;
        this.canLoseHunger = canLoseHunger;
    }

    /**
     * Handle a block break event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (canBreakBlocks) return;
        cancelIfNotAllowedAndNotify(event.getPlayer(), event);
    }

    /**
     * Handle a block place event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (canPlaceBlocks) return;
        cancelIfNotAllowedAndNotify(event.getPlayer(), event);
    }

    /**
     * Handle a drop item event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onDropItem(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (canDropItems) return;
        cancelIfNotAllowedAndNotify(player, event);
    }

    /**
     * Handle a pickup item event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (canPickupItems) return;
        cancelIfNotAllowed(player, event);
    }

    /**
     * Handle a container open event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onContainerOpen(InventoryOpenEvent event) {
        if (canOpenContainers) return;
        if (!(event.getInventory().getHolder() instanceof Container)) return;
        Player player = (Player) event.getPlayer();
        cancelIfNotAllowedAndNotify(player, event);
    }

    /**
     * Handle a player interact event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Material mat = block.getType();
        if (Tag.WOODEN_TRAPDOORS.isTagged(mat) && !canInteractWithTrapdoors) {
            cancelIfNotAllowedAndNotify(player, event);
            return;
        }
        if (Tag.FENCE_GATES.isTagged(mat) && !canInteractWithFenceGates) {
            cancelIfNotAllowedAndNotify(player, event);
            return;
        }
        if (Tag.ALL_SIGNS.isTagged(mat) && !canEditSigns) {
            cancelIfNotAllowedAndNotify(player, event);
        }
    }

    /**
     * Handle a painting break event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if (canBreakBlocks) return;
        if (!(event.getRemover() instanceof Player player)) return;
        cancelIfNotAllowedAndNotify(player, event);
    }

    /**
     * Handle an item frame interact event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (canInteractWithItemFrames) return;
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof ItemFrame) {
            cancelIfNotAllowedAndNotify(player, event);
        }
    }

    /**
     * Handle a food level change event.
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        if (canLoseHunger) return;
        if (!(event.getEntity() instanceof Player player)) return;
        int newLevel = event.getFoodLevel();
        int oldLevel = player.getFoodLevel();
        if (newLevel < oldLevel) {
            // Prevent the player from losing hunger, but allow them to gain hunger
            event.setCancelled(true);
        }
    }

    /**
     * Cancel the given event if the player is not allowed to perform the action.
     * By default, the event is cancelled if the player is not in creative mode.
     *
     * @param player the player
     * @param event  the event
     */
    protected void cancelIfNotAllowed(Player player, Cancellable event) {
        if (player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(true);
    }

    /**
     * Notify the player if the event was cancelled. See {@link #cancelIfNotAllowed(Player, Cancellable)}.
     *
     * @param player the player
     * @param event  the event
     */
    protected void cancelIfNotAllowedAndNotify(Player player, Cancellable event) {
        cancelIfNotAllowed(player, event);
        if (!event.isCancelled()) return;
        Utils.fail(player);
    }
}
