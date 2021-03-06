package net.gearz.jlibrary.base.command;

import net.gearz.jlibrary.base.TPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This is the dispatcher for all commands. It will handle commands and dispatch them to the proper methods.
 */
@SuppressWarnings("UnusedDeclaration")
public class TCommandDispatch implements CommandExecutor {
    /**
     * This is used as a utility to store the order of arguments, and their type for the executor method validation.
     */
    private static final Class[] argumentOrder = {CommandSender.class, TCommandSender.class, TCommand.class, Command.class, String[].class};
    /**
     * The local copy of the plugin
     */
    private TPlugin plugin;
    /**
     * Associates commands to their respective handlers. One handler per command, obviously.
     */
    private HashMap<Command, TCommandHandler> handlers = new HashMap<>();
    /**
     * Associates the commands to their respective methods.
     */
    private HashMap<Command, Method> methods = new HashMap<>();
    /**
     * Associates commands with their metadata. This is easily done without this array, but if you get a variable, best store it.
     */
    private HashMap<Command, TCommand> metas = new HashMap<>();
    /**
     * Constructs the dispatcher
     *
     * @param plugin The plugin this dispatcher is for.
     */
    public TCommandDispatch(TPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Scans a class for valid commands, and places the data in the stores
     *
     * @param commandHandler This is the class to scan
     * @param plugin         A copy of the plugin, normally gotten from this object
     */
    private void scanClass(TCommandHandler commandHandler, TPlugin plugin) {

        Method[] commandHandlerMethods = commandHandler.getClass().getMethods();

        for (Method method : commandHandlerMethods) {
            //Loop through all methods for the class

            PluginCommand cmd;
            //This gets the annotation, otherwise known as the meta
            TCommand annotation = method.getAnnotation(TCommand.class);
            if (annotation == null || // Firstly check if annotation is there, if not:
                    method.getReturnType() != TCommandStatus.class || // Secondly check the method returns TCommandStatus, if not:
                    method.getParameterTypes().length != TCommandDispatch.argumentOrder.length || // Thirdly ->
                    !Arrays.equals(method.getParameterTypes(), TCommandDispatch.argumentOrder) || // and Fourthly; check if the parameters match those specified, if not:
                    (cmd = plugin.getCommand(annotation.name())) == null)  // Assign cmd parameter to the command and also check if it exists, if not:
                continue; // go to next method in the list

            cmd.setExecutor(this); //Sets the executor to this
            cmd.setUsage(annotation.usage()); //Set usage of the command for the help docs

            //Store values
            this.handlers.put(cmd, commandHandler);
            this.methods.put(cmd, method);
            this.metas.put(cmd, annotation);
        }
    }

    /**
     * Registers a handler
     *
     * @param handler The command handler to register
     */
    public void registerHandler(TCommandHandler handler) {
        this.scanClass(handler, this.plugin);
    }

    /**
     * Un-register a handler
     *
     * @param handler Un-registers a handler!
     */
    public void unregisterHandler(TCommandHandler handler) {
        List<Command> commands = new ArrayList<>(); //This is where we will store all commands handled by the class

        for (Command cmd : this.handlers.keySet()) { //Gets the commands handled by the class (populates "commands" (Line 83))
            TCommandHandler handler1 = this.handlers.get(cmd);
            if (handler1.equals(handler1)) commands.add(cmd);
        }
        for (Command cmd : commands) { //Removes associations for all found commands.
            methods.remove(cmd);
            handlers.remove(cmd);
            metas.remove(cmd);
        }
    }

    /**
     * Gets the associated handler to the command.
     *
     * @param command The command in question.
     * @return The handler associated with the command passed.
     */
    public TCommandHandler getHandler(Command command) {
        return handlers.get(command);
    }

    /**
     * Gets the method invoked when the command is executed.
     *
     * @param command The command in question.
     * @return The method associated with the command passed.
     */
    public Method getMethod(Command command) {
        return methods.get(command);
    }

    /**
     * Gets the metadata on the command
     *
     * @param command The command in question.
     * @return The meta associated with the command passed.
     */
    public TCommand getMeta(Command command) {
        return metas.get(command);
    }

    /**
     * Bukkit's onCommand method
     *
     * @param sender  Command sender
     * @param command Command
     * @param s       Label
     * @param strings Args
     * @return Command failure/success
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        //Surround in try/catch to catch-all and then send a friendly response
        try {
            //Gets the handler/meta/method, and validates
            TCommandHandler handler = getHandler(command);
            if (handler == null) {
                sender.sendMessage(ChatColor.RED + "There was no handler found for this command!");
                return true;
            }
            Method method = getMethod(command);
            TCommand meta = getMeta(command);
            //Gets the command sender type (enum value based off instanceof)
            TCommandSender type = getType(sender);

            Boolean validType = false; //Checks if the command sender is valid for the specified senders in the meta.

            for (TCommandSender sender1 : meta.senders()) {
                if (sender1 != type)
                    continue;
                validType = true;

                // Break as we have found what we need
                break;
            }
            //Sends validation statuses for premature termination of the command if the command
            if (!validType) {
                handler.handleCommandStatus(TCommandStatus.WRONG_TARGET, sender, type);
                return true;
            }
            if (!sender.hasPermission(meta.permission())) {
                handler.handleCommandStatus(TCommandStatus.PERMISSIONS, sender, type);
                return true;
            }
            //Invokes the command
            Object invoke = method.invoke(handler, sender, type, meta, command, strings);
            //Validates, stores, casts, and handles the status.
            if (!(invoke instanceof TCommandStatus)) throw new TCommandException("The method did not return a status!");
            TCommandStatus status = (TCommandStatus) invoke;
            if (status == TCommandStatus.HELP) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/" + ChatColor.DARK_PURPLE + meta.name() + " " + ChatColor.AQUA + "- " + ChatColor.DARK_AQUA + meta.usage());
                return true;
            }
            handler.handleCommandStatus(status, sender, type);
        } catch (InvocationTargetException e) {
            //Handles any thrown exceptions
            sender.sendMessage(ChatColor.DARK_RED + "An error occurred internally when executing this command. A detailed log is in the console!");
            sender.sendMessage(String.format("%s%s%s%s:%s%s", ChatColor.RED, ChatColor.ITALIC, e.getClass().getSimpleName(), ChatColor.DARK_AQUA, ChatColor.WHITE, e.getMessage()));
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            sender.sendMessage(ChatColor.RED + "at: " + ChatColor.GREEN + stackTraceElement.getClassName() + ChatColor.DARK_AQUA + ":" + ChatColor.GREEN + stackTraceElement.getLineNumber());
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Gets the type associated with the command sender.
     *
     * @param sender The actual sender object
     * @return The sender type (enum value)
     */
    private TCommandSender getType(CommandSender sender) {
        TCommandSender.fromCommandSender(sender);
    }
}
