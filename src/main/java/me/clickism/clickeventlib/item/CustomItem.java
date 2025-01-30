package me.clickism.clickeventlib.item;

import me.clickism.clickeventlib.ClickEventLib;
import me.clickism.clickeventlib.annotations.Colorized;
import me.clickism.clickeventlib.util.Identifier;
import me.clickism.clickeventlib.util.Utils;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a custom item.
 */
public class CustomItem implements Named {
    /**
     * Key for the item id in the item's persistent data container.
     */
    public final static NamespacedKey ITEM_ID_KEY = ClickEventLib.identifier("item_id").toNamespacedKey();

    private Set<Action> allowedActions = Set.of(
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
    );

    /**
     * Item stack of the custom item.
     */
    protected final ItemStack item;
    private final Identifier id;

    private Consumer<PlayerInteractEvent> onInteract = event -> {};
    private Consumer<PlayerItemConsumeEvent> onConsume = event -> {};
    private Consumer<EntityPickupItemEvent> onPickup = event -> {};

    private final Map<Player, Long> lastInteractionMap = new HashMap<>();

    private boolean immovable = false;
    private boolean useOnInteract = false;
    private boolean useOnConsume = false;

    private long interactCooldown = 0;

    /**
     * Create a new custom item with the given identifier and material.
     * The display name of the item will be the display name of the material.
     *
     * @param id       identifier
     * @param material material of the item
     */
    public CustomItem(Identifier id, Material material) {
        this.id = id;
        this.item = buildItem(id, material);
    }

    /**
     * Creates a new custom item.
     *
     * @param id          identifier
     * @param displayName display name of the item
     * @param material    material of the item
     */
    public CustomItem(Identifier id, String displayName, Material material) {
        this(id, material);
        setName(displayName);
    }

    /**
     * Set the action to be performed when the item is interacted with.
     *
     * @param onInteract action to perform
     * @return this item
     */
    public CustomItem setOnInteract(Consumer<PlayerInteractEvent> onInteract) {
        this.onInteract = onInteract;
        return this;
    }

    /**
     * Set the action to be performed when the item is consumed.
     *
     * @param onConsume action to perform
     * @return this item
     */
    public CustomItem setOnConsume(Consumer<PlayerItemConsumeEvent> onConsume) {
        this.onConsume = onConsume;
        return this;
    }

    /**
     * Set the action to be performed when the item is picked up.
     *
     * @param onPickup action to perform
     * @return this item
     */
    public CustomItem setOnPickup(Consumer<EntityPickupItemEvent> onPickup) {
        this.onPickup = onPickup;
        return this;
    }

    /**
     * Set the item to be removed from the player's inventory after interacting with it.
     * <p>This will inherently cancel the interact event.</p>
     *
     * @return this item
     */
    public CustomItem setUseOnInteract() {
        this.useOnInteract = true;
        return this;
    }

    /**
     * Set the item to be removed from the player's inventory after consuming it.
     * <p>This will inherently cancel the consume event.</p>
     *
     * @return this item
     */
    public CustomItem setUseOnConsume() {
        this.useOnConsume = true;
        return this;
    }

    /**
     * Set the item to be removed from the player's inventory after interacting with it.
     *
     * @param name display name
     * @return this item
     */
    public CustomItem setName(@Colorized String name) {
        return applyToMeta(meta -> meta.setDisplayName(Utils.colorize(name)));
    }

    /**
     * Adds an enchantment to the item with a level of 1.
     *
     * @param enchantment enchantment
     * @return this item
     */
    public CustomItem addEnchant(Enchantment enchantment) {
        return addEnchant(enchantment, 1);
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment enchantment
     * @param level       level of the enchantment
     * @return this item
     */
    public CustomItem addEnchant(Enchantment enchantment, int level) {
        return applyToMeta(meta -> meta.addEnchant(enchantment, level, true));
    }

    /**
     * Sets the custom model data of the item.
     *
     * @param modelData custom model data
     * @return this item
     */
    public CustomItem setModelData(int modelData) {
        return applyToMeta(meta -> meta.setCustomModelData(modelData));
    }

    /**
     * Adds an enchantment glint to the item.
     *
     * @return this item
     */
    public CustomItem addEnchantmentGlint() {
        return applyToMeta(meta -> {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        });
    }

    /**
     * Adds lore to the item.
     *
     * @param lore lore lines
     * @return this item
     */
    public CustomItem setLore(String... lore) {
        List<String> list = new ArrayList<>(lore.length);
        for (String line : lore) {
            list.add(Utils.colorize(line));
        }
        return setLore(list);
    }

    /**
     * Adds lore to the item.
     *
     * @param lore lore
     * @return this item
     */
    public CustomItem setLore(List<String> lore) {
        return applyToMeta(meta -> meta.setLore(lore.stream()
                .map(Utils::colorize)
                .toList()));
    }

    /**
     * Adds a line to the lore of this icon.
     *
     * @param line the line
     * @return this icon
     */
    public CustomItem addLoreLine(@Colorized String line) {
        return applyToMeta(meta -> {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(Utils.colorize(line));
            meta.setLore(lore);
        });
    }

    /**
     * Makes the item immovable in the inventory. The item will keep its slot.
     *
     * @return this item
     */
    public CustomItem setImmovableInInventory() {
        this.immovable = true;
        return this;
    }

    /**
     * Hides the attributes of the item.
     *
     * @return this item
     */
    public CustomItem hideAttributes() {
        return applyToMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES));
    }

    /**
     * Hides potion effects of the item.
     *
     * @return this item
     */
    public CustomItem hidePotionEffects() {
        return applyToMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS));
    }

    /**
     * Hides all attributes of the item.
     *
     * @return this item
     */
    public CustomItem hideAllAttributes() {
        return applyToMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS,
                ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE,
                ItemFlag.HIDE_UNBREAKABLE));
    }

    /**
     * Makes the item unbreakable.
     *
     * @return this item
     */
    public CustomItem setUnbreakable() {
        return applyToMeta(meta -> meta.setUnbreakable(true));
    }

    /**
     * Sets the cooldown for interacting with the item.
     *
     * @param seconds cooldown in seconds
     * @return this item
     */
    public CustomItem setInteractCooldown(int seconds) {
        this.interactCooldown = 1000L * seconds;
        return this;
    }

    /**
     * Sets the allowed actions for interacting with the item.
     *
     * @param allowedActions allowed actions
     * @return this item
     */
    public CustomItem setAllowedActions(Set<Action> allowedActions) {
        this.allowedActions = allowedActions;
        return this;
    }

    /**
     * Applies a consumer to the item's meta.
     *
     * @param consumer consumer to apply
     * @return this item
     */
    public CustomItem applyToMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return this;
        consumer.accept(meta);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Creates a new item from this custom item.
     *
     * @return new item
     */
    public ItemStack createItem() {
        return createItem(1);
    }

    /**
     * Creates a new item from this custom item with the specified amount.
     *
     * @param amount amount of items
     * @return new item
     */
    public ItemStack createItem(int amount) {
        ItemStack item = this.item.clone();
        item.setAmount(amount);
        return item;
    }

    /**
     * Called when a player consumes the item.
     *
     * @param event consume event
     */
    public void onConsume(PlayerItemConsumeEvent event) {
        onConsume.accept(event);
    }

    /**
     * Called when a player interacts with the item.
     *
     * @param event interact event
     */
    public void onInteract(PlayerInteractEvent event) {
        onInteract.accept(event);
    }

    /**
     * Called when an entity picks up the item.
     *
     * @param event pickup event
     */
    public void onPickup(EntityPickupItemEvent event) {
        onPickup.accept(event);
    }

    /**
     * Get the allowed actions for interacting with this item.
     *
     * @return allowed actions
     */
    public Set<Action> getAllowedActions() {
        return allowedActions;
    }

    @Override
    public String getName() {
        return id.toString();
    }

    /**
     * Get the identifier of the item.
     *
     * @return identifier of the item
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Get the display key of the item.
     *
     * @return display key of the item
     */
    public String getDisplayName() {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "";
        return meta.getDisplayName();
    }

    /**
     * Check if the item is immovable.
     *
     * @return true if the item is immovable
     */
    public boolean isImmovable() {
        return immovable;
    }

    /**
     * Check if the item is removed from the player's inventory after interacting with it.
     *
     * @return true if the item is removed from the player's inventory after interacting with it
     */
    public boolean isUseOnInteract() {
        return useOnInteract;
    }

    /**
     * Check if the item is removed from the player's inventory after consuming it.
     *
     * @return true if the item is removed from the player's inventory after consuming it
     */
    public boolean isUseOnConsume() {
        return useOnConsume;
    }

    /**
     * Register an interaction with the item.
     * Used for calculating interact cooldowns.
     *
     * @param player player
     */
    public void registerInteraction(Player player) {
        lastInteractionMap.put(player, System.currentTimeMillis());
    }

    /**
     * Check if the item has an interact cooldown.
     *
     * @param player player
     * @return true if the item has an interact cooldown
     */
    public boolean hasInteractCooldown(Player player) {
        Long lastInteraction = lastInteractionMap.get(player);
        if (lastInteraction == null) return false;
        return System.currentTimeMillis() - lastInteraction < interactCooldown;
    }

    /**
     * Get the interact cooldown in seconds. Will return at least 1 second if the cooldown is active.
     *
     * @param player player
     * @return interact cooldown in seconds, or 0 if there is no cooldown.
     */
    public int getRemainingInteractCooldown(Player player) {
        Long lastInteraction = lastInteractionMap.get(player);
        if (lastInteraction == null) return 0;
        long remaining = interactCooldown - (System.currentTimeMillis() - lastInteraction);
        if (remaining < 0) return 0;
        return (int) Math.ceil((double) remaining / 1000);
    }

    /**
     * Check if the custom item is similar to another item.
     *
     * @param other other item
     * @return true if the items are similar
     */
    public boolean isOf(ItemStack other) {
        return isOf(other, id);
    }

    /**
     * Builds an item stack with the required tag. See {@link #ITEM_ID_KEY}.
     */
    private static ItemStack buildItem(Identifier id, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, id.toString());
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Check if an item is of a custom item with the given id.
     *
     * @param item item to check
     * @param id   id of the custom item
     * @return true if the item is of the custom item
     */
    public static boolean isOf(ItemStack item, Identifier id) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        String itemId = meta.getPersistentDataContainer().get(ITEM_ID_KEY, PersistentDataType.STRING);
        return id.toString().equals(itemId);
    }
}
