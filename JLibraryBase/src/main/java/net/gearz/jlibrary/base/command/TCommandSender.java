package net.gearz.jlibrary.base.command;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * All command sender types
 */
public enum TCommandSender {
    /**
     * The block command sender. This is when a Commandblock sends a command. Unsure on targets.
     */
    Block(BlockCommandSender.class),
    /**
     * This is when a player sends a command.
     */
    Player(org.bukkit.entity.Player.class),
    /**
     * This is when the console sends a command.
     */
    Console(ConsoleCommandSender.class);

    private final Class<? extends CommandSender> commandSender;

    private static volatile Map<Class<? extends CommandSender>, TCommandSender> LOOKUP = new HashMap<>();

    static {
        populateLookup();
    }

    TCommandSender(Class<? extends CommandSender> commandSender) {
        this.commandSender = commandSender;
    }

    private static void populateLookup() {
        for(TCommandSender tCommandSender : TCommandSender.values()) {
            LOOKUP.put(tCommandSender.getCommandSender(), tCommandSender);
        }
    }

    public static TCommandSender fromCommandSender(CommandSender commandSender) {
        return fromCommandSender(commandSender.getClass());
    }

    public static TCommandSender fromCommandSender(Class<? extends CommandSender> commandSender) {
        return LOOKUP.get(commandSender);
    }

    public Class<? extends CommandSender> getCommandSender() {
        return commandSender;
    }
}
