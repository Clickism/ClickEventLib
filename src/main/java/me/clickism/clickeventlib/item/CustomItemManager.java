package me.clickism.clickeventlib.item;

import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.util.NamedCollection;
import me.clickism.clickeventlib.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Custom item manager.
 */
public class CustomItemManager implements Listener {
    private final NamedCollection<CustomItem> items = new NamedCollection<>(new ArrayList<>());

    /**
     * Create a new custom item manager.
     *
     * @param plugin plugin
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public CustomItemManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Register a custom item.
     *
     * @param item custom item to register
     * @return the registered custom item
     */
    public CustomItem register(CustomItem item) {
        if (item.getId().equals(VanillaItem.VANILLA_IDENTIFIER)) {
            throw new IllegalArgumentException("You can't register a vanilla custom item.");
        }
        items.add(item);
        return item;
    }

    /**
     * Get all registered custom items.
     *
     * @return collection of all registered custom items
     */
    public NamedCollection<CustomItem> getItems() {
        return items;
    }

    /**
     * Get a custom item by its identifier.
     *
     * @param item custom item to get
     * @return the custom item, or null if not found
     */
    @Nullable
    public CustomItem getCustomItemOf(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        String id = meta.getPersistentDataContainer().get(CustomItem.ITEM_ID_KEY, PersistentDataType.STRING);
        return items.get(id);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return;
        CustomItem customItem = getCustomItemOf(item);
        if (customItem == null) return;
        if (!customItem.getAllowedActions().contains(event.getAction())) return;
        Player player = event.getPlayer();
        if (customItem.hasInteractCooldown(player)) {
            int remaining = customItem.getRemainingInteractCooldown(player);
            MessageType.FAIL.send(player, "Please wait &l" + remaining + "&c second(s) to use that.");
            event.setCancelled(true);
            return;
        }
        customItem.onInteract(event);
        customItem.registerInteraction(player);
        if (customItem.isUseOnInteract()) {
            event.setCancelled(true);
            Utils.removeItem(player, item, 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        CustomItem customItem = getCustomItemOf(item);
        if (customItem == null) return;
        customItem.onConsume(event);
        if (customItem.isUseOnConsume()) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            Utils.removeItem(player, item, 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        CustomItem customItem = getCustomItemOf(item);
        if (customItem == null) return;
        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        // Prevent moving immovable item inside the inventory
        if (customItem.isImmovable()) {
            Utils.fail(player); 
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onPickup(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        CustomItem customItem = getCustomItemOf(item);
        if (customItem == null) return;
        customItem.onPickup(event);
    }
}
