package me.clickism.clickeventlib.commands.trigger;

import me.clickism.subcommandapi.command.*;
import me.clickism.subcommandapi.argument.EnumArgument;
import me.clickism.subcommandapi.argument.MultipleSelectionArgument;
import me.clickism.clickeventlib.debug.LocationDisplayer;
import me.clickism.clickeventlib.location.SafeLocation;
import me.clickism.clickeventlib.trigger.Trigger;
import me.clickism.clickeventlib.trigger.TriggerBox;
import me.clickism.clickeventlib.trigger.TriggerManager;
import me.clickism.clickeventlib.util.FormatUtils;
import me.clickism.clickeventlib.util.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

class TriggerDisplaySubcommand extends PlayerOnlySubcommand {
    private enum DisplayType {
        BOX, POINT
    }

    private static final Material MIN_POS_MATERIAL = Material.RED_STAINED_GLASS;
    private static final Color MIN_POS_COLOR = Color.RED;
    private static final Material MAX_POS_MATERIAL = Material.BLUE_STAINED_GLASS;
    private static final Color MAX_POS_COLOR = Color.BLUE;

    private record DisplayColor(Material material, Color glowColor) {
    }

    private static final DisplayColor[] COLORS = new DisplayColor[]{
            new DisplayColor(Material.RED_STAINED_GLASS, Color.RED),
            new DisplayColor(Material.BLUE_STAINED_GLASS, Color.BLUE),
            new DisplayColor(Material.LIME_STAINED_GLASS, Color.LIME),
            new DisplayColor(Material.YELLOW_STAINED_GLASS, Color.YELLOW),
            new DisplayColor(Material.PURPLE_STAINED_GLASS, Color.PURPLE),
            new DisplayColor(Material.ORANGE_STAINED_GLASS, Color.ORANGE),
            new DisplayColor(Material.GREEN_STAINED_GLASS, Color.GREEN),
            new DisplayColor(Material.MAGENTA_STAINED_GLASS, Color.FUCHSIA),
            new DisplayColor(Material.CYAN_STAINED_GLASS, Color.AQUA),
            new DisplayColor(Material.BROWN_STAINED_GLASS, Color.MAROON),
            new DisplayColor(Material.LIGHT_BLUE_STAINED_GLASS, Color.NAVY)
    };

    private static final EnumArgument<DisplayType> DISPLAY_TYPE_ARGUMENT =
            new EnumArgument<>("display_type", false, DisplayType.class);

    private final MultipleSelectionArgument<Trigger> triggersArgument;

    private final LocationDisplayer displayer = new LocationDisplayer();

    private final TriggerManager triggerManager;
    private final Map<Trigger, DisplayColor> colorMap = new HashMap<>();

    private int glassIndex = 0;

    public TriggerDisplaySubcommand(TriggerManager triggerManager) {
        super("display", true);
        this.triggerManager = triggerManager;
        this.triggersArgument = new MultipleSelectionArgument<>("trigger", false, triggerManager.getTriggers());
        addArgument(triggersArgument);
        addArgument(DISPLAY_TYPE_ARGUMENT);
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        Collection<TriggerBox> triggerBoxes = triggerManager.getTriggerBoxes();
        if (displayer.isDisplaying()) {
            displayer.removeDisplays();
            return CommandResult.warning("Removed all trigger box displays.");
        }
        World world = player.getWorld();
        Set<Trigger> triggers = new HashSet<>(
                argHandler.getOrNull(triggersArgument) != null
                        ? argHandler.get(triggersArgument)
                        : triggerManager.getTriggers());
        DisplayType displayType = argHandler.getOrDefault(DISPLAY_TYPE_ARGUMENT, DisplayType.BOX);
        displayTriggerBoxes(triggerBoxes, triggers, world, displayType);
        displayTriggerInteractions(triggerManager.getTriggerInteractionMap(), triggers);
        return CommandResult.success("Displayed trigger boxes.");
    }

    private void displayTriggerBoxes(Collection<TriggerBox> triggerBoxes, Set<Trigger> triggers, World world, DisplayType displayType) {
        triggerBoxes.forEach(box -> {
            if (!triggers.contains(box.getTrigger())) return;
            displayTriggerBox(box, world, displayType);
        });
    }

    private void displayTriggerInteractions(Map<SafeLocation, Trigger> interactionMap, Set<Trigger> triggers) {
        interactionMap.forEach((safeLocation, trigger) -> {
            if (!triggers.contains(trigger)) return;
            Location location = safeLocation.getLocation();
            if (location == null) return;
            displayInteraction(trigger, location);
        });
    }

    private void displayTriggerBox(TriggerBox box, World world, DisplayType displayType) {
        Trigger trigger = box.getTrigger();

        Location minPos = box.getMinPos().toLocation(world);
        Location maxPos = box.getMaxPos().toLocation(world);
        String infoText = Utils.colorize(FormatUtils.formatTriggerBox(box));

        switch (displayType) {
            case BOX -> displayBox(trigger, minPos, maxPos, infoText);
            case POINT -> displayPoints(minPos, maxPos, infoText);
        }
    }

    private void displayInteraction(Trigger trigger, Location location) {
        DisplayColor color = getOrCreateColor(trigger);
        Color glowColor = color.glowColor;
        String triggerName = ChatColor.BOLD + trigger.getName();
        displayer.displayLocation(location, color.material, glowColor, triggerName);
    }

    private void displayBox(Trigger trigger, Location minPos, Location maxPos, String infoText) {
        DisplayColor color = getOrCreateColor(trigger);
        displayer.displayBox(minPos, maxPos, color.material, infoText);
    }

    private void displayPoints(Location minPos, Location maxPos, String infoText) {
        displayer.displayLocation(minPos, MIN_POS_MATERIAL, MIN_POS_COLOR, infoText);
        displayer.displayLocation(maxPos.add(-1, -1, -1), MAX_POS_MATERIAL, MAX_POS_COLOR, infoText);
    }

    private DisplayColor getOrCreateColor(Trigger trigger) {
        return colorMap.computeIfAbsent(trigger, k -> COLORS[glassIndex++ % COLORS.length]);
    }

    private int getId(Map<Trigger, Map<Integer, Integer>> idMap, TriggerBox box) {
        Trigger trigger = box.getTrigger();
        int z = box.getZ();
        return idMap.computeIfAbsent(trigger, k -> new HashMap<>()).merge(z, 1, Integer::sum);
    }
}
