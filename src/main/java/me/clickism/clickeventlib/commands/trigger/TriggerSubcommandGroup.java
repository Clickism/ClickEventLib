package me.clickism.clickeventlib.commands.trigger;

import me.clickism.clickeventlib.command.SubcommandGroup;
import me.clickism.clickeventlib.trigger.TriggerManager;

/**
 * Subcommand group for trigger-related commands.
 */
public class TriggerSubcommandGroup extends SubcommandGroup {
    /**
     * Creates a new trigger subcommand group.
     *
     * @param triggerManager the trigger manager
     */
    public TriggerSubcommandGroup(TriggerManager triggerManager) {
        super("trigger", true);
        addSubcommand(new SubcommandGroup("interaction", true)
                .addSubcommand(new TriggerInteractionAddSubcommand(triggerManager))
                .addSubcommand(new TriggerInteractionRemoveAllSubcommand(triggerManager))
        );
        addSubcommand(new SubcommandGroup("box", true)
                .addSubcommand(new TriggerBoxAddSubcommand(triggerManager))
                .addSubcommand(new TriggerBoxRemoveSubcommand(triggerManager))
                .addSubcommand(new TriggerBoxRemoveInsideSubcommand(triggerManager))
                .addSubcommand(new TriggerBoxRemoveAllSubcommand(triggerManager))
                .addSubcommand(new TriggerBoxSelectSubcommand(triggerManager))
                .addSubcommand(new TriggerBoxCheckInsideSubcommand(triggerManager))
        );
        addSubcommand(new TriggerDisplaySubcommand(triggerManager));
        addSubcommand(new TriggerCancelSubcommand(triggerManager.getSelectionManager()));
        addSubcommand(new TriggerBypassSubcommand(triggerManager));
    }
}
