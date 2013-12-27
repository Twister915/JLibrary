package me.joey.library.netcommand;

import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.HashMap;

/**
 * The NetCommand class is used solely to build commands to send. :D
 */
public class NetCommand {
    /**
     * The arguments of the command we're going to send.
     */
    @Getter private HashMap<String, Object> args;
    /**
     * The name of the command.
     */
    @Getter private String name;

    /**
     * Stores the Jedis
     */
    private Jedis jedis;

    private String channel;

    /**
     * Private constructor
     * @param name The name.
     */
    private NetCommand(String name, Jedis jedis, String channel) {
        this.name = name;
        this.args = new HashMap<>();
        this.jedis = jedis;
        this.channel = channel;
    }

    /**
     * Create a command with the name.
     * @param name The name of the command.
     * @return The NetCommand object. Ready for building.
     */
    public static NetCommand beginBuilding(Jedis jedis, String channel, String name) {
        return new NetCommand(name, jedis, channel);
    }

    /**
     * Add an argument
     * @param arg With name
     * @param o and value
     * @return this object.
     */
    public NetCommand withArg(String arg, Object o) {
        this.args.put(arg, o);
        return this;
    }

    /**
     * Commits the command, and broadcasts it in the channel.
     */
    public void send() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", name);
            JSONObject argsObject = new JSONObject(args);
            jsonObject.put("data", argsObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        jedis.publish(this.channel, jsonObject.toString());
    }
}
