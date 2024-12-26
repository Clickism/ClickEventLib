package me.clickism.clickeventlib.trigger;

import me.clickism.clickeventlib.location.SafeLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Trigger selection manager.
 */
public class TriggerSelectionManager {
    /**
     * Creates a new trigger selection manager.
     */
    public TriggerSelectionManager() {
    }

    /**
     * Position type.
     */
    public enum PosType {
        /**
         * First position.
         */
        POS1,
        /**
         * Second position.
         */
        POS2
    }

    private final Map<Player, TriggerBoxBuilder> builderMap = new HashMap<>();
    private final Map<Player, Trigger> interactionMap = new HashMap<>();

    /**
     * Starts selecting a trigger box.
     *
     * @param player  player
     * @param trigger trigger
     * @param z       z
     */
    public void startSelectingBox(Player player, Trigger trigger, int z) {
        builderMap.put(player, new TriggerBoxBuilder(trigger, z));
    }

    /**
     * Starts selecting a trigger interaction.
     *
     * @param player  player
     * @param trigger trigger
     */
    public void startSelectingInteraction(Player player, Trigger trigger) {
        interactionMap.put(player, trigger);
    }

    /**
     * Stops selecting a trigger.
     *
     * @param player player
     * @return the trigger if the player was selecting a trigger, null otherwise
     */
    public Trigger stopSelecting(Player player) {
        TriggerBoxBuilder removedBoxBuilder = builderMap.remove(player);
        Trigger removedTrigger = interactionMap.remove(player);
        if (removedBoxBuilder != null) {
            return removedBoxBuilder.getTrigger();
        }
        return removedTrigger;
    }

    /**
     * Checks if a player is selecting a trigger box.
     *
     * @param player player
     * @return true if the player is selecting a trigger box, false otherwise
     */
    public boolean isSelectingBox(Player player) {
        return builderMap.containsKey(player);
    }

    /**
     * Checks if a player is selecting a trigger interaction.
     *
     * @param player player
     * @return true if the player is selecting a trigger interaction, false otherwise
     */
    public boolean isSelectingInteraction(Player player) {
        return interactionMap.containsKey(player);
    }

    /**
     * Selects a position for the trigger box.
     *
     * @param player         player
     * @param location       location
     * @param posType        position type
     * @param triggerManager trigger manager to register the trigger with if the box can be built
     * @return trigger box if the box can be built, null otherwise
     */
    @Nullable
    public TriggerBox selectPosAndBuild(Player player, Location location, PosType posType, TriggerManager triggerManager) {
        if (!builderMap.containsKey(player)) {
            throw new IllegalStateException("Player isn't selecting a trigger box");
        }
        TriggerBoxBuilder builder = builderMap.get(player);
        switch (posType) {
            case POS1 -> builder.setPos1(location);
            case POS2 -> builder.setPos2(location);
        }
        World world = location.getWorld();
        if (world == null) return null;
        Trigger trigger = builder.getTrigger();
        int id = triggerManager.getNextId(trigger);
        if (!builder.canBuild()) return null;
        // Build and register the trigger box
        TriggerBox box = builder.build(id, world.getName());
        triggerManager.registerTriggerBox(box);
        stopSelecting(player);
        return box;
    }

    /**
     * Selects an interaction location for a trigger.
     *
     * @param player         player
     * @param location       location
     * @param triggerManager trigger manager
     */
    public void selectInteraction(Player player, Location location, TriggerManager triggerManager) {
        if (!interactionMap.containsKey(player)) {
            throw new IllegalStateException("Player isn't selecting an interaction");
        }
        Trigger trigger = interactionMap.get(player);
        triggerManager.registerTriggerInteraction(SafeLocation.of(location), trigger);
        stopSelecting(player);
    }
}
