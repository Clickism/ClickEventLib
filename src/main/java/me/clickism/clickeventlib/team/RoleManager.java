package me.clickism.clickeventlib.team;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages roles and prefixes.
 */
public class RoleManager implements Listener {

    private final JSONDataManager dataManager;

    private final NamedCollection<Role> roles = new NamedCollection<>(new ArrayList<>());
    private final Map<UUID, String> roleMap = new HashMap<>();

    /**
     * Creates a new role manager with the given plugin and file name.
     *
     * @param plugin   the plugin to create the role manager for
     * @param fileName the name of the file to store the roles
     * @throws IOException if an I/O error occurs
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public RoleManager(JavaPlugin plugin, String fileName) throws IOException {
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        load();
    }

    /**
     * Registers a role.
     *
     * @param role the role to register
     */
    public void registerRole(Role role) {
        roles.add(role);
    }

    /**
     * Sets a player's role.
     * <p>
     * Make sure to call {@link #save()} after giving a role.
     *
     * @param uuid the UUID of the player
     * @param role the role to give
     */
    public void setRole(UUID uuid, Role role) {
        roleMap.put(uuid, role.getName());
    }

    /**
     * Removes the role from a player.
     * <p>
     * Make sure to call {@link #save()} after removing a role.
     *
     * @param uuid the UUID of the player
     */
    public void removeRole(UUID uuid) {
        roleMap.remove(uuid);
    }

    /**
     * Gets the prefix of a player based on their roles.
     *
     * @param uuid the UUID of the player
     * @return the prefix of the player
     */
    public String getPrefix(UUID uuid) {
        Role role = getRole(uuid);
        if (role == null) return "";
        return role.prefix();
    }

    /**
     * Gets the role of a player.
     *
     * @param uuid the UUID of the player
     * @return the role of the player
     */
    @Nullable
    public Role getRole(UUID uuid) {
        String roleName = roleMap.get(uuid);
        if (roleName == null) return null;
        return roles.get(roleName);
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public NamedCollection<Role> getRoles() {
        return roles;
    }

    /**
     * Saves the roles to the file.
     */
    public void save() {
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        roleMap.forEach((uuid, role) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("uuid", uuid.toString());
            entry.addProperty("role", role);
            array.add(entry);
        });
        json.add("players", array);
        dataManager.save(json);
    }

    private void load() {
        JsonObject root = dataManager.getRoot();
        if (!root.has("players")) return;
        JsonArray array = root.getAsJsonArray("players");
        array.forEach(element -> {
            JsonObject entry = element.getAsJsonObject();
            UUID uuid = UUID.fromString(entry.get("uuid").getAsString());
            String role = entry.get("role").getAsString();
            roleMap.put(uuid, role);
        });
    }
}
