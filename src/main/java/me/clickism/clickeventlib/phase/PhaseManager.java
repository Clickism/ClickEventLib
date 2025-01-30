package me.clickism.clickeventlib.phase;

import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.location.EventWorld;
import me.clickism.clickeventlib.location.WorldManager;
import me.clickism.clickeventlib.phase.group.PhaseGroup;
import me.clickism.clickeventlib.serialization.JSONDataManager;
import me.clickism.subcommandapi.util.NamedCollection;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
// TODO : TELEPORT TO WORLD SPAWN ON DEATH EVENT PRIORITY

/**
 * Handles the phases of an event and the lobby.
 */
public class PhaseManager implements Listener {
    private final JavaPlugin plugin;

    private final NamedCollection<PhaseGroup> phaseGroups = new NamedCollection<>(new ArrayList<>());

    private @Nullable PhaseGroup currentPhaseGroup;
    private @Nullable Phase currentPhase;

    private long secondsPassed = 0;

    private final EventBar eventBar;
    private final WorldManager worldManager;

    private final JSONDataManager dataManager;

    /**
     * Create a new phase manager.
     *
     * @param plugin       plugin
     * @param eventBar     event bar
     * @param fileName     file name
     * @param worldManager world manager used to manage event worlds
     * @throws IOException if an I/O error occurs
     */
    @AutoRegistered(type = RegistryType.EVENT)
    public PhaseManager(JavaPlugin plugin, @Nullable EventBar eventBar, String fileName, WorldManager worldManager) throws IOException {
        this.plugin = plugin;
        this.eventBar = eventBar;
        this.worldManager = worldManager;
        this.dataManager = new JSONDataManager(plugin, plugin.getDataFolder(), fileName);
        Server server = plugin.getServer();
        server.getScheduler().runTaskTimer(plugin, this::tick, 20, 20);
        server.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Register a phase group.
     *
     * @param phaseGroup phase group to register
     * @return this phase manager
     */
    public PhaseManager register(PhaseGroup phaseGroup) {
        phaseGroups.add(phaseGroup);
        for (Phase phase : phaseGroup.getPhases()) {
            if (tryLoad(phase)) break;
        }
        return this;
    }

    private void tick() {
        if (currentPhase == null) return;
        updateBar();
        currentPhase.onTick(secondsPassed);
        secondsPassed++;
        if (currentPhase.getDuration() == Phase.INFINITE_DURATION) {
            save();
            return;
        }
        if (secondsPassed >= currentPhase.getDuration()) {
            Phase next = startNextPhase();
            // Skip phases with 0 duration
            while (next != null && next.getDuration() == 0) {
                next = startNextPhase();
            }
        }
        save();
    }

    /**
     * Start the next phase in the current phase group.
     *
     * @return next phase, or null if the last phase was reached
     */
    @Nullable
    public Phase startNextPhase() {
        Phase nextPhase = getNextPhase();
        if (nextPhase == null) return null;
        startPhase(nextPhase);
        return nextPhase;
    }

    /**
     * Set and start the given phase.
     *
     * @param phase phase to start
     * @throws IllegalArgumentException if the phase is not in the current phase group
     */
    public void startPhase(Phase phase) throws IllegalArgumentException {
        setPhase(phase, true);
    }

    /**
     * Set and initialize the given phase without starting it.
     *
     * @param phase phase to set
     * @throws IllegalArgumentException if the phase is not in the current phase group
     */
    public void setPhase(Phase phase) throws IllegalArgumentException {
        setPhase(phase, false);
    }

    private void setPhase(Phase phase, boolean start) throws IllegalArgumentException {
        if (currentPhaseGroup == null) {
            throw new IllegalArgumentException("No phase group set");
        }
        if (currentPhase != null) {
            endPhase(currentPhase);
        }
        currentPhase = phase;
        currentPhaseGroup.setCurrentPhase(phase);
        initPhase(phase, start);
        save();
    }

    /**
     * Calls the necessary methods to set and start a phase.
     *
     * @param phase phase to set
     * @param start whether to start the phase
     */
    private void initPhase(Phase phase, boolean start) {
        phase.onSet();
        phase.getEventWorlds().forEach(this::setupEventWorld);
        if (start) {
            phase.onStart();
        }
        phase.getPhaseListener().register(plugin);
    }

    /**
     * Calls the necessary methods to end a phase.
     *
     * @param phase phase to end
     */
    private void endPhase(Phase phase) {
        phase.onEnd();
        phase.getPhaseListener().unregister();
    }

    /**
     * Load and set up the given event world.
     *
     * @param world event world to set up
     */
    private void setupEventWorld(EventWorld world) {
        String worldName = world.getName();
        try {
            worldManager.importWorld(worldName);
        } catch (Exception exception) {
            Bukkit.getLogger().severe("Event world " + worldName + " couldn't be set up: " + exception.getMessage());
            return;
        }
        world.setupWorld();
    }

    /**
     * Get the next phase, or null if the last phase was reached.
     *
     * @return next phase
     */
    @Nullable
    public Phase getNextPhase() {
        if (currentPhaseGroup == null) return null;
        return currentPhaseGroup.getNextPhase();
    }

    /**
     * Get the current phase.
     *
     * @return current phase
     */
    @Nullable
    public Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Set and start the given phase group.
     * Runs the start script.
     *
     * @param phaseGroup phase group to start
     */
    public void startPhaseGroup(PhaseGroup phaseGroup) {
        setPhaseGroup(phaseGroup);
        phaseGroup.getStartScript().run();
        startNextPhase();
    }

    /**
     * Set the current phase group.
     * Does NOT run the start script.
     *
     * @param phaseGroup phase group
     */
    public void setPhaseGroup(PhaseGroup phaseGroup) {
        this.currentPhaseGroup = phaseGroup;
    }

    /**
     * Get the current phase group.
     *
     * @return current phase group
     */
    @Nullable
    public PhaseGroup getCurrentPhaseGroup() {
        return currentPhaseGroup;
    }

    /**
     * Get the named collection of phases.
     *
     * @return named phases
     */
    public NamedCollection<PhaseGroup> getPhaseGroups() {
        return phaseGroups;
    }

    /**
     * Get the phases in the current group.
     *
     * @return phases in the current group
     */
    public NamedCollection<Phase> getPhasesInCurrentGroup() {
        if (currentPhaseGroup == null) {
            return NamedCollection.of();
        }
        return currentPhaseGroup.getPhases();
    }

    /**
     * Get the seconds remaining in the current phase.
     *
     * @return seconds left
     */
    public long getSecondsRemaining() {
        if (currentPhase == null) return 0;
        long duration = currentPhase.getDuration();
        return duration - secondsPassed;
    }

    /**
     * Get the seconds passed in the current phase.
     *
     * @return seconds passed
     */
    public long getSecondsPassed() {
        return secondsPassed;
    }

    /**
     * Set the seconds remaining in the current phase.
     *
     * @param secondsRemaining seconds
     */
    public void setSecondsRemaining(long secondsRemaining) {
        long duration = currentPhase.getDuration();
        this.secondsPassed = duration - secondsRemaining;
        updateBar();
    }

    /**
     * Set the seconds passed in the current phase.
     *
     * @param secondsPassed seconds
     */
    public void setSecondsPassed(long secondsPassed) {
        this.secondsPassed = secondsPassed;
    }

    private void updateBar() {
        if (eventBar == null) return;
        if (currentPhase == null) return;
        long secondsRemaining = getSecondsRemaining();
        String title = currentPhase.getEventBarTitle(secondsRemaining);
        eventBar.setTitle(title);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        if (currentPhase == null) return;
        Player player = event.getPlayer();
        currentPhase.onJoinServer(player);
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        if (currentPhase == null) return;
        Player player = event.getPlayer();
        currentPhase.onLeaveServer(player);
    }

    private void save() {
        JsonObject json = new JsonObject();
        json.addProperty("phase", currentPhase != null ? currentPhase.getName() : null);
        json.addProperty("seconds", secondsPassed);
        dataManager.save(json);
    }

    /**
     * Try to load the phase and seconds from the data manager.
     *
     * @param phase phase to load
     * @return true if the phase was loaded, false otherwise
     */
    private boolean tryLoad(Phase phase) {
        JsonObject root = dataManager.getRoot();
        if (!root.has("phase")) return false;
        String phaseName = root.get("phase").getAsString();
        long seconds = root.get("seconds").getAsLong();
        if (!phase.getName().equals(phaseName)) return false;
        initPhase(phase, false);
        setSecondsPassed(seconds);
        return true;
    }
}
