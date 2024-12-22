package me.clickism.clickeventlib.phase;

import com.google.gson.JsonObject;
import me.clickism.clickeventlib.annotations.AutoRegistered;
import me.clickism.clickeventlib.annotations.RegistryType;
import me.clickism.clickeventlib.location.EventWorld;
import me.clickism.clickeventlib.location.WorldManager;
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
import java.util.*;

/**
 * Handles the phases of an event and the lobby.
 */
public class PhaseManager implements Listener {
    private final JavaPlugin plugin;

    private final NamedCollection<Phase> namedPhases = new NamedCollection<>(new LinkedList<>());
    private final List<Phase> phases = new ArrayList<>();
    private final Deque<Phase> phaseQueue = new ArrayDeque<>();

    private Phase currentPhase;

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
     * Register a phase.
     * <p>
     * Phases will be executed in the registration order.
     *
     * @param phase phase to register
     * @return this phase manager
     */
    public PhaseManager register(Phase phase) {
        phases.add(phase);
        namedPhases.add(phase);
        phaseQueue.add(phase);
        tryLoad(phase);
        return this;
    }

    /**
     * Register a phase.
     * <p>
     * This phase will be put in the beginning of the phase queue.
     *
     * @param phase phase to register
     * @return this phase manager
     */
    public PhaseManager registerFirst(Phase phase) {
        phases.add(0, phase);
        namedPhases.add(phase);
        phaseQueue.addFirst(phase);
        tryLoad(phase);
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
     * Set and start the new phase.
     * <p>
     * This will reconstruct the phase queue to start from the new phase.
     *
     * @param phase new phase
     */
    public void startPhase(Phase phase) {
        reconstructQueue(phase); // Reconstruct queue to start from the new phase
        phaseQueue.poll(); // Remove the current phase from the queue
        startNextPhase(phase); // Start the new phase
    }

    /**
     * Start the next phase in the queue or the next phase override of the current phase
     * if it has one.
     *
     * @return next phase, or null if the last phase was reached
     */
    @Nullable
    public Phase startNextPhase() {
        return startNextPhase(null);
    }

    /**
     * Start the given phase, or the next phase in the queue or the next phase override of the current phase
     * if the given phase is null.
     *
     * @param toStart phase to start or null
     * @return started phase, or null if the last phase was reached
     */
    private Phase startNextPhase(@Nullable Phase toStart) {
        if (currentPhase != null) {
            endPhase(currentPhase);
        }
        Phase phase = toStart != null ? toStart : getNextPhaseOrOverride();
        if (phase == null) {
            return null;
        }
        currentPhase = phase;
        secondsPassed = 0;
        setPhase(phase, true);
        return phase;
    }

    /**
     * Get the next phase in the queue or the next phase override of the current phase.
     *
     * @return next phase, or null if the last phase was reached and there is no phase override
     */
    @Nullable
    private Phase getNextPhaseOrOverride() {
        Phase phase = null;
        if (currentPhase != null) {
            phase = currentPhase.getNextPhaseOverride();
        }
        if (phase != null) {
            return phase;
        }
        if (phaseQueue.isEmpty()) {
            // No phase override and no next phase in the queue
            return null;
        }
        return phaseQueue.poll();
    }

    /**
     * Set the current phase, this won't start the phase.
     *
     * @param phase new phase
     */
    public void setPhase(Phase phase) {
        reconstructQueue(phase); // Reconstruct queue to start from the new phase
        if (currentPhase != null) {
            endPhase(currentPhase);
        }
        currentPhase = phaseQueue.poll();
        if (currentPhase == null) return;
        setPhase(currentPhase, false);
    }

    /**
     * Calls the necessary methods to set and start a phase.
     *
     * @param phase phase to set
     * @param start whether to start the phase
     */
    private void setPhase(Phase phase, boolean start) {
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
     * Reconstruct the phase queue to start from the given phase.
     *
     * @param startingPhase starting phase
     */
    private void reconstructQueue(Phase startingPhase) {
        phaseQueue.clear();
        boolean add = false;
        for (Phase phase : phases) {
            if (!add && phase.equals(startingPhase)) {
                add = true;
            }
            if (add) {
                phaseQueue.add(phase);
            }
        }
    }

    /**
     * Get the next phase, or null if the last phase was reached.
     *
     * @return next phase
     */
    @Nullable
    public Phase getNextPhase() {
        return phaseQueue.peek();
    }

    /**
     * Get the list of phases.
     *
     * @return phases
     */
    public List<Phase> getPhaseList() {
        return phases;
    }

    /**
     * Get the named collection of phases.
     *
     * @return named phases
     */
    public NamedCollection<Phase> getPhases() {
        return namedPhases;
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

    private void tryLoad(Phase phase) {
        JsonObject root = dataManager.getRoot();
        if (!root.has("phase")) return;
        String phaseName = root.get("phase").getAsString();
        long seconds = root.get("seconds").getAsLong();
        if (phase.getName().equals(phaseName)) {
            setPhase(phase);
            setSecondsPassed(seconds);
        }
    }
}
