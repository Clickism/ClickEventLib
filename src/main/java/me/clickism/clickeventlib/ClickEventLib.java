package me.clickism.clickeventlib;

import me.clickism.clickeventlib.chat.ChatManager;
import me.clickism.clickeventlib.commands.EventCommandManager;
import me.clickism.clickeventlib.commands.chat.ChatSubcommandGroup;
import me.clickism.clickeventlib.commands.debug.DebugSubcommandGroup;
import me.clickism.clickeventlib.commands.item.ItemSubcommandGroup;
import me.clickism.clickeventlib.commands.leaderboard.LeaderboardSubcommandGroup;
import me.clickism.clickeventlib.commands.location.LocationSubcommandGroup;
import me.clickism.clickeventlib.commands.phase.PhaseSubcommandGroup;
import me.clickism.clickeventlib.commands.role.RoleSubcommandGroup;
import me.clickism.clickeventlib.commands.statistic.StatisticSubcommandGroup;
import me.clickism.clickeventlib.commands.team.InviteSubcommand;
import me.clickism.clickeventlib.commands.team.JoinSubcommand;
import me.clickism.clickeventlib.commands.team.LeaveSubcommand;
import me.clickism.clickeventlib.commands.world.WorldSubcommandGroup;
import me.clickism.clickeventlib.debug.LocationDisplayer;
import me.clickism.clickeventlib.item.CustomItemManager;
import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import me.clickism.clickeventlib.location.EventLocationManager;
import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.clickeventlib.phase.EventBar;
import me.clickism.clickeventlib.phase.PhaseManager;
import me.clickism.clickeventlib.serialization.AutoSaver;
import me.clickism.clickeventlib.statistic.StatisticManager;
import me.clickism.clickeventlib.statistic.Statistics;
import me.clickism.clickeventlib.statistic.UUIDManager;
import me.clickism.clickeventlib.team.EventTeam;
import me.clickism.clickeventlib.team.Role;
import me.clickism.clickeventlib.team.RoleManager;
import me.clickism.clickeventlib.team.TeamManager;
import me.clickism.clickeventlib.util.Identifier;
import me.clickism.subcommandapi.command.CommandManager;
import me.clickism.subcommandapi.command.SubcommandGroup;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * ClickEventLib class.
 */
public final class ClickEventLib extends JavaPlugin {
    /**
     * Singleton instance of the plugin.
     */
    public static ClickEventLib INSTANCE;
    /**
     * Logger for the plugin.
     */
    public static Logger LOGGER;

    /**
     * ClickEventLib constructor.
     */
    public ClickEventLib() {
    }

    /**
     * Namespace of the plugin
     */
    public static final String NAMESPACE = "event";

    private PhaseManager phaseManager;
    private CustomItemManager customItemManager;
    private CommandManager commandManager;
    private RoleManager roleManager;
    private ChatManager chatManager;
    private WorldManager worldManager;

    private EventLocationManager locationManager;
    private StatisticManager statisticManager;
    private LeaderboardManager leaderboardManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();
        AutoSaver autoSaver = new AutoSaver(this);
        try {
            this.worldManager = new WorldManager(this, "worlds.json");
            EventBar eventBar = new EventBar(this, EventBar.DEFAULT_TITLE);
            this.phaseManager = new PhaseManager(this, eventBar, "phase.json", worldManager);
            this.customItemManager = new CustomItemManager(this);
            this.commandManager = new EventCommandManager();
            this.locationManager = new EventLocationManager(this, "locations.json");
            this.statisticManager = new StatisticManager(this, autoSaver, 30, "statistics.json");
            this.leaderboardManager = new LeaderboardManager(this, autoSaver, 30, "leaderboards.json");
            this.roleManager = new RoleManager(this, "roles.json");
            this.getServer().getPluginManager().registerEvents(TeamManager.INSTANCE, this);
            this.chatManager = new ChatManager(this, roleManager);
            UUIDManager.createInstance(this, autoSaver);
        } catch (Exception exception) {
            getLogger().severe("Failed to load data: " + exception.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        autoSaver.register();
        registerCommands();
        Role.registerRoles(roleManager);
        Statistics.registerStatistics(statisticManager, leaderboardManager);
        getLogger().info("ClickEventLib activated.");
    }

    @Override
    public void onDisable() {
        LocationDisplayer.removeGlobalDisplays();
        getLogger().info("ClickEventLib deactivated.");
    }

    /**
     * Registers the commands for the plugin.
     */
    private void registerCommands() {
        commandManager.registerCommand(new SubcommandGroup("event", true)
                .addSubcommand(new PhaseSubcommandGroup(phaseManager))
                .addSubcommand(new DebugSubcommandGroup())
                .addSubcommand(new LocationSubcommandGroup(locationManager))
                .addSubcommand(new ItemSubcommandGroup(customItemManager))
                .addSubcommand(new WorldSubcommandGroup(worldManager))
                .addSubcommand(new StatisticSubcommandGroup(statisticManager))
                .addSubcommand(new LeaderboardSubcommandGroup(leaderboardManager))
                .addSubcommand(new RoleSubcommandGroup(roleManager, chatManager))
        );
        commandManager.registerCommand(new ChatSubcommandGroup(chatManager));
        commandManager.registerCommand(new JoinSubcommand(chatManager, false));
        commandManager.registerCommand(new LeaveSubcommand(chatManager, false));
        commandManager.registerCommand(new InviteSubcommand(false));
    }

    /**
     * Gets the main phase manager.
     *
     * @return the phase manager
     */
    public PhaseManager getPhaseManager() {
        return phaseManager;
    }

    /**
     * Gets the main custom item manager.
     *
     * @return the custom item manager
     */
    public CustomItemManager getCustomItemManager() {
        return customItemManager;
    }

    /**
     * Gets the main command manager.
     *
     * @return the command manager
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Gets the main chat manager.
     *
     * @return the chat manager
     */
    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Gets the main world manager.
     *
     * @return the world manager
     */
    public WorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * Gets the main role manager.
     *
     * @return the role manager
     */
    public RoleManager getRoleManager() {
        return roleManager;
    }

    /**
     * Creates an identifier with the given key and the ID of the plugin.
     *
     * @param name key for the identifier
     * @return identifier
     */
    public static Identifier identifier(String name) {
        return new Identifier(NAMESPACE, name);
    }
}
