package me.clickism.clickeventlib.phase;

import me.clickism.clickeventlib.location.EventLocation;
import me.clickism.clickeventlib.location.EventWorld;
import me.clickism.clickeventlib.util.Identifier;
import me.clickism.subcommandapi.util.Named;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a phase (state) in the event.
 */
public abstract class Phase implements Named {
    /**
     * Represents infinite duration for a phase.
     */
    public static final long INFINITE_DURATION = -1;

    private final Identifier id;
    private final long seconds;
    private final List<EventWorld> worlds;
    private final PhaseListener listener;

    private final Map<Long, Runnable> actions = new HashMap<>();

    /**
     * Creates a new phase with the given id and infinite duration.
     *
     * @param id     id of the phase
     * @param worlds event worlds that this phase uses
     */
    public Phase(Identifier id, List<EventWorld> worlds) {
        this(id, INFINITE_DURATION, worlds);
    }

    /**
     * Creates a new phase with the given id and duration in seconds.
     *
     * @param id      id of the phase
     * @param seconds duration of the phase in seconds
     * @param worlds  event worlds that this phase uses
     */
    public Phase(Identifier id, long seconds, List<EventWorld> worlds) {
        this(id, seconds, worlds, new PhaseListener() {});
    }

    /**
     * Creates a new phase with the given id, duration in seconds and phase listener.
     *
     * @param id       id of the phase
     * @param seconds  duration of the phase in seconds
     * @param worlds   event worlds that this phase uses
     * @param listener phase listener
     */
    public Phase(Identifier id, long seconds, List<EventWorld> worlds, PhaseListener listener) {
        this.id = id;
        this.seconds = seconds;
        this.worlds = worlds;
        this.listener = listener;
    }

    /**
     * Called when the phase starts.
     * <p>
     * <b>Tip:</b> Use this for giving items/announcements/teleporting players etc.
     * <p>
     * <b>Warning:</b> Don't add actions here when possible, use {@link #onSet()} instead.
     */
    public abstract void onStart();

    /**
     * Called after the phase is set/loaded.
     * Also called before the phase is started.
     * <p>
     * <b>Tip:</b> Use this for actions, gamerules/settings etc.
     */
    public abstract void onSet();

    /**
     * Called when the phase ends and a new phase is started or a new phase is set.
     */
    public abstract void onEnd();

    /**
     * Called when a player joins the server while this phase is active.
     *
     * @param player player that joined the server
     */
    public abstract void onJoinServer(Player player);

    /**
     * Called when a player leaves the server while this phase is active.
     *
     * @param player player that left the server
     */
    public abstract void onLeaveServer(Player player);

    /**
     * Gets the required event locations for the phase.
     * The "/... phase set" command will give a warning if any of the locations aren't set.
     *
     * @return required event locations
     */
    public abstract List<EventLocation> getRequiredEventLocations();

    /**
     * Called when the phase timer ticks. Handles the actions for the given seconds.
     *
     * @param secondsPassed seconds passed since the phase started
     */
    public void onTick(long secondsPassed) {
        Runnable action = actions.get(secondsPassed);
        if (action != null) {
            action.run();
        }
    }

    /**
     * Adds an action to be executed when the timer reaches the given seconds.
     *
     * @param seconds time in seconds
     * @param action  action to be executed
     */
    protected void addAction(long seconds, Runnable action) {
        actions.put(seconds, action);
    }

    /**
     * Gets the event worlds that this phase uses.
     *
     * @return event worlds
     */
    public List<EventWorld> getEventWorlds() {
        return worlds;
    }

    /**
     * Gets the phase listener for the phase.
     *
     * @return phase listener
     */
    public PhaseListener getPhaseListener() {
        return listener;
    }

    /**
     * Gets the duration of the phase in seconds.
     *
     * @return duration of the phase in seconds
     */
    public long getDuration() {
        return seconds;
    }

    /**
     * @return Name of the phase
     */
    @Override
    public String getName() {
        return id.toString();
    }

    /**
     * Gets the identifier of the phase.
     *
     * @return phase identifier
     */
    public Identifier getId() {
        return id;
    }

    /**
     * Gets the bar title with the given timer.
     *
     * @param seconds current timer seconds
     * @return event bar title
     */
    public abstract String getEventBarTitle(long seconds);

}
