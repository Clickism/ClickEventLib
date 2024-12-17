package me.clickism.clickeventlib.commands.trigger;

import me.clickism.subcommandapi.command.*;
import me.clickism.clickeventlib.trigger.TriggerManager;
import org.bukkit.entity.Player;

class TriggerBypassSubcommand extends PlayerOnlySubcommand {
    private final TriggerManager triggerManager;
    public TriggerBypassSubcommand(TriggerManager triggerManager) {
        super("bypass", true);
        this.triggerManager = triggerManager;
    }

    @Override
    protected CommandResult execute(CommandStack trace, Player player, ArgumentHandler argHandler) throws CommandException {
        if (triggerManager.hasBypass(player)) {
            triggerManager.removeBypass(player);
            return CommandResult.warning("Disabled &lbypass mode.");
        } else {
            triggerManager.addBypass(player);
            return CommandResult.success("Enabled &lbypass mode.");
        }
    }
}
