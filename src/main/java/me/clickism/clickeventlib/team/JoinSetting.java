package me.clickism.clickeventlib.team;

/**
 * The setting that determines how players can team teams.
 */
public enum JoinSetting {
    /**
     * Everyone can team any team freely.
     */
    EVERYONE_OPEN,
    /**
     * Everyone can team an empty team, but one of the members need to invite you if it's not empty.
     */
    EVERYONE_INVITE,
    /**
     * Teams are managed by operators.
     */
    OPERATOR
}
