package me.clickism.clickeventlib.team;

import me.clickism.clickeventlib.util.Named;
import me.clickism.clickeventlib.util.Utils;

/**
 * Represents a role.
 *
 * @param name   the name of the role
 * @param prefix the prefix of the role
 */
public record Role(String name, String prefix) implements Named {
    /**
     * The admin role. Corresponds to the "@Staff" role on Discord.
     */
    public static final Role STAFF = new Role("staff",
            Utils.colorize("&x&F&C&3&F&3&F[&x&F&6&3&C&4&CS&x&F&0&3&A&5&8t&x&E&A&3&7&6&5a&x&E&3&3&4&7&1f&x&D&D&3&2&7&Ef&x&D&7&2&F&8&A] &r"));
    /**
     * The staff role. Corresponds to the "@Event Manager" role on Discord.
     */
    public static final Role MANAGER = new Role("manager",
            Utils.colorize("&x&9&8&6&3&E&7[&x&A&2&5&E&D&DM&x&A&B&5&9&D&3a&x&B&5&5&4&C&9n&x&B&E&4&F&B&Fa&x&C&8&4&9&B&4g&x&D&1&4&4&A&Ae&x&D&B&3&F&A&0r&x&E&4&3&A&9&6] &r"));
    /**
     * The event intern role. Corresponds to the "@Event Intern" role on Discord.
     */
    public static final Role INTERN = new Role("intern",
            Utils.colorize("&x&A&A&A&5&F&F[&x&B&6&9&D&F&6I&x&C&2&9&6&E&Cn&x&C&E&8&E&E&3t&x&D&B&8&7&D&9e&x&E&7&7&F&D&0r&x&F&3&7&8&C&6n&x&F&F&7&0&B&D] &r"));

    /**
     * Register the default roles.
     *
     * @param roleManager the role manager to register the roles with
     */
    public static void registerRoles(RoleManager roleManager) {
        roleManager.registerRole(STAFF);
        roleManager.registerRole(MANAGER);
        roleManager.registerRole(INTERN);
    }

    @Override
    public String getName() {
        return name;
    }
}
