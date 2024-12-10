package me.clickism.clickeventlib.statistic;

import me.clickism.clickeventlib.chat.MessageType;
import me.clickism.clickeventlib.leaderboard.LeaderboardManager;
import org.bukkit.entity.Player;

/**
 * A class that holds statistics.
 */
public class Statistics {
    /**
     * No constructor for static class.
     */
    private Statistics() {
    }

    /**
     * XP Message type.
     */
    private static final MessageType XP_MESSAGE = new MessageType("&b[↑] &3", "&8< &b↑ &3%s &8>") {
        @Override
        public void playSound(Player player) {
            player.playSound(player, "general.experience", 1, 1);
        }
    };

    /**
     * XP Statistic.
     */
    public static final Currency XP = new Currency("xp", 0) {
        @Override
        public void sendMessage(Player player, Integer increment) {
            XP_MESSAGE.send(player, "You gained &b&l" + increment + " Experience!");
        }
    };

    /**
     * Register statistics.
     *
     * @param statisticManager   statistic manager
     * @param leaderboardManager leaderboard manager
     */
    public static void registerStatistics(StatisticManager statisticManager, LeaderboardManager leaderboardManager) {
        statisticManager.register(XP);
        leaderboardManager.registerProvider(XP);
    }
}
