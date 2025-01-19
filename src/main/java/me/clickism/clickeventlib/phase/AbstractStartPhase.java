package me.clickism.clickeventlib.phase;

import me.clickism.clickeventlib.ClickEventLib;
import me.clickism.clickeventlib.location.EventWorld;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Abstract class used for easing the implementation of a start phase.
 */
public abstract class AbstractStartPhase extends Phase {
    /**
     * Creates a new start phase.
     *
     * @param worlds list of event worlds
     */
    public AbstractStartPhase(List<EventWorld> worlds) {
        super(ClickEventLib.identifier("start"), 0, worlds);
    }

    @Override
    public void onSet() {
        // Do nothing
    }

    @Override
    public void onEnd() {
        // Do nothing
    }

    @Override
    public void onJoinServer(Player player) {
        // Do nothing
    }

    @Override
    public void onLeaveServer(Player player) {
        // Do nothing
    }

    @Override
    public String getEventBarTitle(long seconds) {
        return EventBar.DEFAULT_TITLE;
    }
}
