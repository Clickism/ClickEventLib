package me.clickism.clickeventlib.command.argument;

import me.clickism.clickeventlib.command.CommandException;
import me.clickism.clickeventlib.command.Subcommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an argument that is a subcommand.
 */
public class SubcommandArgument extends Argument<Subcommand> {
    private final List<Subcommand> subcommands;

    /**
     * Create a new subcommand argument with the given subcommands.
     *
     * @param subcommands the subcommands
     * @param required    whether the argument is required
     */
    public SubcommandArgument(List<Subcommand> subcommands, boolean required) {
        super("subcommand", required);
        this.subcommands = subcommands;
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, String arg) {
        return subcommands.stream()
                .filter(s -> s.canExecute(sender))
                .map(Subcommand::getLabel)
                .collect(Collectors.toList());
    }

    @Override
    public Subcommand parse(CommandSender sender, String arg) {
        return subcommands.stream()
                .filter(subcommand -> subcommand.getLabel().equalsIgnoreCase(arg))
                .findFirst()
                .orElseThrow(() -> new CommandException("Invalid subcommand: &l" + arg));
    }

    @Override
    public String getHint() {
        return "<" + subcommands.stream()
                .map(Subcommand::getLabel)
                .collect(Collectors.joining("/"))
                + ">";
    }
}
