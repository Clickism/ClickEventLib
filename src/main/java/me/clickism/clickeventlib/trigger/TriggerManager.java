package me.clickism.clickeventlib.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.debug.DebugPropertyRegistration;
import me.clickism.clickeventlib.item.CustomItemManager;
import me.clickism.clickeventlib.location.SafeLocation;
import me.clickism.clickeventlib.property.BooleanProperty;
import me.clickism.clickeventlib.property.Property;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.clickeventlib.util.Utils;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Trigger manager.
 */
public class TriggerManager implements Listener {
    /**
     * Enum that represents the cause of a trigger.
     */
    private enum TriggerCause {
        MOVE, TELEPORT
    }

    private static final Property<Boolean> TRIGGER_OUTPUT = DebugPropertyRegistration.register(
            new BooleanProperty("trigger_output", false)
    );

    private final TriggerSelectionManager selectionManager = new TriggerSelectionManager();
    private final TriggerBoxSelectorItem boxSelectorItem;
    private final TriggerInteractionSelectorItem interactionSelectorItem;

    private final Set<Player> bypassedPlayers = new HashSet<>();

    private final NamedCollection<Trigger> triggers = new NamedCollection<>(new ArrayList<>());

    private final Map<Trigger, List<TriggerBox>> triggerToBoxesMap = new HashMap<>();
    private final NamedCollection<TriggerBox> triggerBoxes = new NamedCollection<>(new ArrayList<>());

    private final Map<SafeLocation, Trigger> triggerInteractionMap = new HashMap<>();

    private final Map<Trigger, Integer> nextIdMap = new HashMap<>();

    private final JSONDataManager jsonDataManager;

    /**
     * Create a new trigger manager.
     *
     * @param plugin      plugin to register events with
     * @param itemManager custom item manager to register the trigger selector item with
     * @param namespace   namespace of the plugin used to register the selector item
     * @param fileName    name of the file to save the trigger boxes to
     * @throws IOException if the data couldn't be loaded
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public TriggerManager(JavaPlugin plugin, CustomItemManager itemManager, String namespace, String fileName) throws IOException {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.boxSelectorItem = new TriggerBoxSelectorItem(namespace, this, selectionManager);
        this.interactionSelectorItem = new TriggerInteractionSelectorItem(namespace, this, selectionManager);
        itemManager.register(boxSelectorItem);
        itemManager.register(interactionSelectorItem);
        this.jsonDataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
    }

    /**
     * Get the next ID for a trigger.
     *
     * @param trigger trigger
     * @return next ID
     */
    public int getNextId(Trigger trigger) {
        return nextIdMap.getOrDefault(trigger, 0);
    }

    /**
     * Register a trigger.
     *
     * @param trigger trigger to register
     * @return the registered trigger
     */
    public Trigger registerTrigger(Trigger trigger) {
        triggers.add(trigger);
        tryLoad(trigger);
        return trigger;
    }

    /**
     * Register a trigger with onEnter action.
     *
     * @param name    trigger name
     * @param onEnter action to perform when player enters the trigger
     * @return the registered trigger
     */
    public Trigger registerTrigger(String name, BiConsumer<Player, TriggerBox> onEnter) {
        return registerTrigger(new EnterOnlyTrigger(name) {
            @Override
            public void onEnter(Player player, @Nullable TriggerBox box) {
                onEnter.accept(player, box);
            }
        });
    }

    /**
     * Register a trigger with onEnter and onExit actions.
     *
     * @param name    trigger name
     * @param onEnter action to perform when player enters the trigger
     * @param onExit  action to perform when player exits the trigger
     * @return the registered trigger
     */
    public Trigger registerTrigger(String name,
                                   BiConsumer<Player, TriggerBox> onEnter,
                                   BiConsumer<Player, TriggerBox> onExit) {
        return registerTrigger(new Trigger(name) {
            @Override
            public void onEnter(Player player, @Nullable TriggerBox box) {
                onEnter.accept(player, box);
            }

            @Override
            public void onExit(Player player, @Nullable TriggerBox box) {
                onExit.accept(player, box);
            }
        });
    }

    /**
     * Register a trigger with onEnter, onExit, and onTeleportExit actions.
     *
     * @param name           trigger name
     * @param onEnter        action to perform when player enters the trigger
     * @param onExit         action to perform when player exits the trigger
     * @param onTeleportExit action to perform when player exits the trigger by teleporting
     * @return the registered trigger
     */
    public Trigger registerTrigger(String name,
                                   BiConsumer<Player, TriggerBox> onEnter,
                                   BiConsumer<Player, TriggerBox> onExit,
                                   BiConsumer<Player, TriggerBox> onTeleportExit) {
        return registerTrigger(new Trigger(name) {
            @Override
            public void onEnter(Player player, @Nullable TriggerBox box) {
                onEnter.accept(player, box);
            }

            @Override
            public void onExit(Player player, @Nullable TriggerBox box) {
                onExit.accept(player, box);
            }

            @Override
            public void onTeleportExit(Player player, @Nullable TriggerBox box) {
                onTeleportExit.accept(player, box);
            }
        });
    }

    /**
     * Register a trigger box.
     *
     * @param box trigger box to register
     */
    public void registerTriggerBox(TriggerBox box) {
        addTriggerBox(box);
        sortTriggerBoxes();
        save();
    }

    /**
     * Only adds a trigger box to the map without sorting.
     *
     * @param box trigger box to add
     */
    private void addTriggerBox(TriggerBox box) {
        Trigger trigger = box.getTrigger();
        triggerToBoxesMap.computeIfAbsent(trigger, k -> new ArrayList<>()).add(box);
        triggerBoxes.add(box);
        nextIdMap.put(trigger, box.getId() + 1);
    }

    /**
     * Sorts the trigger boxes based on Z-coordinates in the world.
     */
    private void sortTriggerBoxes() {
        triggerBoxes.sort(Comparator.comparingInt(TriggerBox::getZ));
    }

    /**
     * Unregisters a trigger box.
     *
     * @param box trigger box to unregister
     */
    public void unregisterTriggerBox(TriggerBox box) {
        triggerBoxes.remove(box);
        List<TriggerBox> boxes = triggerToBoxesMap.get(box.getTrigger());
        if (boxes != null) {
            boxes.remove(box);
        }
        save();
    }

    /**
     * Register an interaction.
     *
     * @param location location of the interaction
     * @param trigger  trigger to perform when interacting
     */
    public void registerTriggerInteraction(SafeLocation location, Trigger trigger) {
        addTriggerInteraction(location, trigger);
        save();
    }

    /**
     * Add an interaction without saving.
     *
     * @param location location of the interaction
     * @param trigger  trigger to perform when interacting
     */
    private void addTriggerInteraction(SafeLocation location, Trigger trigger) {
        triggerInteractionMap.put(location, trigger);
    }

    /**
     * Unregister an interaction.
     *
     * @param location location of the interaction
     */
    public void unregisterTriggerInteraction(SafeLocation location) {
        triggerInteractionMap.remove(location);
        save();
    }

    /**
     * Get the list of all triggers.
     *
     * @return collection of all triggers
     */
    public NamedCollection<Trigger> getTriggers() {
        return triggers;
    }

    /**
     * Get the list of all trigger boxes.
     *
     * @return list of all trigger boxes
     */
    public NamedCollection<TriggerBox> getTriggerBoxes() {
        return triggerBoxes;
    }

    /**
     * Get the map of all trigger interactions.
     *
     * @return map of all trigger interactions
     */
    public Map<SafeLocation, Trigger> getTriggerInteractionMap() {
        return triggerInteractionMap;
    }

    /**
     * Remove all trigger boxes.
     */
    public void clearTriggerBoxesAndSave() {
        triggerBoxes.clear();
        triggerToBoxesMap.clear();
        save();
    }

    /**
     * Remove all trigger boxes with the given trigger type.
     *
     * @param trigger trigger to remove
     */
    public void clearTriggerBoxesAndSave(Trigger trigger) {
        clearTriggerBoxes(trigger);
        save();
    }
    
    private void clearTriggerBoxes(Trigger trigger) {
        triggerBoxes.removeIf(box -> box.getTrigger().equals(trigger));
        triggerToBoxesMap.remove(trigger);
    }

    /**
     * Remove all trigger interactions.
     */
    public void clearTriggerInteractionsAndSave() {
        triggerInteractionMap.clear();
        save();
    }

    /**
     * Remove all trigger interactions with the given trigger type.
     *
     * @param trigger trigger to remove
     */
    public void clearTriggerInteractionsAndSave(Trigger trigger) {
        clearTriggerInteractions(trigger);
        save();
    }
    
    private void clearTriggerInteractions(Trigger trigger) {
        triggerInteractionMap.entrySet().removeIf(entry -> entry.getValue().equals(trigger));
    }

    /**
     * Get the selection manager.
     *
     * @return selection manager
     */
    public TriggerSelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * Get the box trigger selector item.
     *
     * @return box trigger selector item
     */
    public TriggerBoxSelectorItem getBoxSelectorItem() {
        return boxSelectorItem;
    }

    /**
     * Get the interaction trigger selector item.
     *
     * @return interaction trigger selector item
     */
    public TriggerInteractionSelectorItem getInteractionSelectorItem() {
        return interactionSelectorItem;
    }

    /**
     * Check if the player is inside any trigger box that has the given trigger.
     *
     * @param player  player to check
     * @param trigger trigger to check
     * @return true if the player is inside a box that has this trigger
     */
    public boolean isInTrigger(Player player, Trigger trigger) {
        return isInTrigger(player.getLocation(), trigger);
    }

    /**
     * Check if the location is inside any trigger box that has the given trigger.
     *
     * @param location location to check
     * @param trigger  trigger to check
     * @return true if the location is inside a box that has this trigger
     */
    public boolean isInTrigger(Location location, Trigger trigger) {
        World world = location.getWorld();
        if (world == null) return false;
        List<TriggerBox> boxes = triggerToBoxesMap.get(trigger);
        if (boxes == null) return false;
        for (TriggerBox box : boxes) {
            if (box.isInside(location)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Bypass all triggers for the player.
     *
     * @param player player to bypass triggers
     */
    public void addBypass(Player player) {
        bypassedPlayers.add(player);
    }

    /**
     * Stop bypassing triggers.
     *
     * @param player player to stop bypassing triggers
     */
    public void removeBypass(Player player) {
        bypassedPlayers.remove(player);
    }

    /**
     * Check if the player is bypassing triggers.
     *
     * @param player player to check
     * @return true if the player is bypassing triggers
     */
    public boolean hasBypass(Player player) {
        return bypassedPlayers.contains(player);
    }

    /**
     * Handle trigger interactions.
     */
    @EventHandler(ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Trigger trigger = getTriggerInteractionAtLocation(block.getLocation());
        if (trigger == null) return;
        if (isButtonAndPowered(block)) return;
        Player player = event.getPlayer();
        if (hasBypass(player)) {
            MessageType.WARN.sendActionbarSilently(player, "You have &nbypass mode&e enabled. Do &6&l/... trigger bypass &eto disable.");
            return;
        }
        trigger.onEnter(player, null);
    }

    private boolean isButtonAndPowered(Block block) {
        if (!Tag.BUTTONS.isTagged(block.getType())) return false;
        Directional directional = (Directional) block.getBlockData();
        return block.getRelative(directional.getFacing()).isBlockIndirectlyPowered();
    }

    private Trigger getTriggerInteractionAtLocation(Location location) {
        SafeLocation safeLocation = new SafeLocation(location);
        return triggerInteractionMap.get(safeLocation);
    }

    @EventHandler(ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Trigger trigger = getTriggerInteractionAtLocation(location);
        if (trigger == null) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!player.isOp()) {
            Utils.fail(player);
            return;
        }
        String triggerName = trigger.getName();
        if (!player.isSneaking()) {
            MessageType.FAIL.send(player,
                    "There is a trigger interaction &l" + triggerName + "&c at this block. " +
                            "&lShift + break &cto remove this interaction first.");
            return;
        }
        SafeLocation safeLocation = new SafeLocation(location);
        unregisterTriggerInteraction(safeLocation);
        MessageType.WARN.send(player, "Removed trigger interaction: &l" + triggerName);
    }

    @EventHandler(ignoreCancelled = true)
    private void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        handleMove(player, from, to, TriggerCause.TELEPORT);
    }

    /**
     * Handle trigger entering/exiting
     */
    @EventHandler(ignoreCancelled = true)
    private void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();
        handleMove(player, from, to, TriggerCause.MOVE);
    }

    private void handleMove(Player player, Location from, Location to, TriggerCause cause) {
        if (to == null) return;
        BlockVector toBlockVector = to.toVector().toBlockVector();
        BlockVector fromBlockVector = from.toVector().toBlockVector();
        if (toBlockVector.equals(fromBlockVector)) return;
        if (selectionManager.isSelectingBox(player)) {
            MessageType.WARN.sendActionbarSilently(player, "You are selecting a trigger box");
            return;
        }
        if (hasBypass(player)) {
            MessageType.WARN.sendActionbarSilently(player,
                    "You have &nbypass mode&e enabled. Do &6&l/... trigger bypass &eto disable.");
            return;
        }
        TriggerBox enteredBox = findEnteredBox(triggerBoxes, to);
        TriggerBox exitedBox = findExitedBox(triggerBoxes, from);
        handleTriggerBoxes(player, enteredBox, exitedBox, from, to, cause);
    }

    private TriggerBox findEnteredBox(Collection<TriggerBox> triggerBoxes, Location to) {
        for (TriggerBox box : triggerBoxes) {
            if (box.isInside(to)) {
                return box;
            }
        }
        return null;
    }

    private TriggerBox findExitedBox(Collection<TriggerBox> triggerBoxes, Location from) {
        for (TriggerBox box : triggerBoxes) {
            if (box.isInside(from)) {
                return box;
            }
        }
        return null;
    }

    /**
     * Handle entering/exiting the trigger boxes. Will do nothing if the entered and exited triggers are the same.
     *
     * @param player     player entering/exiting the trigger box
     * @param enteredBox trigger box the player entered
     * @param exitedBox  trigger box the player exited
     */
    private void handleTriggerBoxes(Player player,
                                    @Nullable TriggerBox enteredBox, @Nullable TriggerBox exitedBox,
                                    Location from, Location to, TriggerCause cause) {
        Trigger enteredTrigger = enteredBox != null ? enteredBox.getTrigger() : null;
        Trigger exitedTrigger = exitedBox != null ? exitedBox.getTrigger() : null;
        if (enteredTrigger == exitedTrigger) return;
        if (exitedBox != null && !isInTrigger(to, exitedTrigger)) {
            exitAndLogTriggerBox(player, exitedBox, cause);
        }
        if (cause == TriggerCause.TELEPORT) return;
        if (enteredBox != null && !isInTrigger(from, enteredTrigger)) {
            enterAndLogTriggerBox(player, enteredBox);
        }
    }

    private void enterAndLogTriggerBox(Player player, TriggerBox box) {
        box.enter(player);
        if (TRIGGER_OUTPUT.get()) {
            String triggerName = box.getTrigger().getName();
            MessageType.CONFIRM.send(player, "Entering: &l" + triggerName);
        }
    }

    private void exitAndLogTriggerBox(Player player, TriggerBox box, TriggerCause cause) {
        switch (cause) {
            case MOVE -> box.exit(player);
            case TELEPORT -> box.teleportExit(player);
        }
        if (TRIGGER_OUTPUT.get()) {
            String triggerName = box.getTrigger().getName();
            MessageType.WARN.send(player, "Exiting: &l" + triggerName);
        }
    }

    /**
     * Try to load the trigger boxes/interactions that have the given trigger.
     *
     * @param trigger trigger to load the boxes/interactions for
     */
    private void tryLoad(Trigger trigger) {
        JsonObject root = jsonDataManager.getRoot();
        String triggerName = trigger.getName();
        if (!root.has(triggerName)) return;
        clearTriggerBoxes(trigger);
        clearTriggerInteractions(trigger);

        JsonObject triggerNode = root.getAsJsonObject(triggerName);
        JsonArray boxes = triggerNode.getAsJsonArray("boxes");
        // Load boxes
        for (JsonElement boxesElement : boxes) {
            JsonObject box = boxesElement.getAsJsonObject();
            int id = getNextId(trigger);
            TriggerBox triggerBox = fromJson(box, id, trigger);
            addTriggerBox(triggerBox);
        }
        sortTriggerBoxes();
        // Load interactions
        JsonArray interactions = triggerNode.getAsJsonArray("interactions");
        for (JsonElement interaction : interactions) {
            SafeLocation location = JSONDataManager.GSON.fromJson(interaction, SafeLocation.class);
            addTriggerInteraction(location, trigger);
        }
    }

    /**
     * Save the trigger boxes to the data file.
     */
    public void save() {
        JsonObject json = new JsonObject();
        triggers.forEach(trigger -> {
            JsonObject triggerNode = new JsonObject();
            triggerNode.add("boxes", new JsonArray());
            triggerNode.add("interactions", new JsonArray());
            json.add(trigger.getName(), triggerNode);
        });
        triggerBoxes.forEach(box -> {
            JsonObject boxNode = toJson(box);
            String triggerName = box.getTrigger().getName();
            JsonArray boxesArray = json.getAsJsonObject(triggerName).getAsJsonArray("boxes");
            boxesArray.add(boxNode);
        });
        triggerInteractionMap.forEach((location, trigger) -> {
            JsonElement locationNode = JSONDataManager.GSON.toJsonTree(location, SafeLocation.class);
            String triggerName = trigger.getName();
            JsonArray interactionsArray = json.getAsJsonObject(triggerName).getAsJsonArray("interactions");
            interactionsArray.add(locationNode);
        });
        jsonDataManager.save(json);
    }

    private TriggerBox fromJson(JsonObject json, int id, Trigger trigger) {
        String worldName = json.get("world").getAsString();
        int z = json.get("z").getAsInt();
        int minX = json.get("minX").getAsInt();
        int minY = json.get("minY").getAsInt();
        int minZ = json.get("minZ").getAsInt();
        int maxX = json.get("maxX").getAsInt();
        int maxY = json.get("maxY").getAsInt();
        int maxZ = json.get("maxZ").getAsInt();
        return new TriggerBox(id, worldName, trigger, z, minX, minY, minZ, maxX, maxY, maxZ);
    }

    private JsonObject toJson(TriggerBox triggerBox) {
        JsonObject json = new JsonObject();
        json.addProperty("world", triggerBox.getWorldName());
        json.addProperty("z", triggerBox.getZ());
        BlockVector minPos = triggerBox.getMinPos();
        BlockVector maxPos = triggerBox.getMaxPos();
        json.addProperty("minX", minPos.getBlockX());
        json.addProperty("minY", minPos.getBlockY());
        json.addProperty("minZ", minPos.getBlockZ());
        json.addProperty("maxX", maxPos.getBlockX());
        json.addProperty("maxY", maxPos.getBlockY());
        json.addProperty("maxZ", maxPos.getBlockZ());
        return json;
    }
}
