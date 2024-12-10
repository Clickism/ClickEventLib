package me.clickism.clickeventlib.leaderboard;

import me.clickism.clickeventlib.util.Named;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides leaderboard entries.
 */
public interface LeaderboardEntryProvider extends Named {
    /**
     * Get the leaderboard entries.
     *
     * @return the leaderboard entries
     */
    @NotNull
    List<Map.Entry<UUID, String>> getLeaderboardEntries();
}
