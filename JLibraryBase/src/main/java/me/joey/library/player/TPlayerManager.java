package me.joey.library.player;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import me.joey.library.TPlugin;
import me.joey.library.player.cooldowns.TCooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 *
 */
public class TPlayerManager implements Listener {
    private HashMap<String, TPlayer> players = new HashMap<>();
    private DBCollection collection = null;
    private DB database = null;
    private static TPlayerManager instance;
    private TPlugin plugin;
    public TPlayerManager(AuthenticationDetails details, TPlugin tpLugin) {
        TPlayerManager.instance = this;
        Logger logger = tpLugin.getLogger();
        MongoClient databaseClient;
        try {
            databaseClient = details.getClient();
            logger.info("Attempting a connection to the MongoDB!");
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            logger.severe("Failed to connect!");
            return;
        }
        if (details.getDatabase() == null || details.getPlayerCollection() == null) {
            logger.severe("Cannot continue, data is null for connection!");
            return;
        }
        this.database = databaseClient.getDB(details.getDatabase());
        if (this.database == null) {
            logger.severe("Failed to connect!");
            return;
        }
        this.collection = this.database.getCollection(details.getPlayerCollection());
        logger.info("Connected to MongoDB!");
        TCooldownManager.database = database;
        this.plugin = tpLugin;
    }

    public static TPlayerManager getInstance() {
        return instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onLogin(PlayerJoinEvent event) {
        TPlayerJoinEvent tPlayerJoinEvent = new TPlayerJoinEvent(this.addPlayer(event.getPlayer()));
        Bukkit.getPluginManager().callEvent(tPlayerJoinEvent);
        event.setJoinMessage(tPlayerJoinEvent.getJoinMessage());
    }
    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onDisconnect(PlayerQuitEvent event) {
        if (!this.players.containsKey(event.getPlayer().getName())) return;
        TPlayerDisconnectEvent tPlayerDisconnectEvent = new TPlayerDisconnectEvent(players.get(event.getPlayer().getName()));
        Bukkit.getPluginManager().callEvent(tPlayerDisconnectEvent);
        event.setQuitMessage(tPlayerDisconnectEvent.getQuitMessage());
        players.get(event.getPlayer().getName()).disconnected();
        players.remove(event.getPlayer().getName());
    }
    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onKick(PlayerKickEvent event) {
        if (!this.players.containsKey(event.getPlayer().getName())) return;
        TPlayerDisconnectEvent tPlayerDisconnectEvent = new TPlayerDisconnectEvent(players.get(event.getPlayer().getName()));
        Bukkit.getPluginManager().callEvent(tPlayerDisconnectEvent);
        event.setLeaveMessage(tPlayerDisconnectEvent.getQuitMessage());
        players.get(event.getPlayer().getName()).disconnected();
        players.remove(event.getPlayer().getName());
    }
    public TPlayer getPlayer(Player player) {
        return players.get(player.getName());
    }
    public TPlayer addPlayer(Player player) {
        if (this.players.containsKey(player.getName())) this.players.remove(player.getName());
        TPlayer tPlayer = new TPlayer(player, this.plugin);
        this.players.put(player.getName(), tPlayer);
        return tPlayer;
    }
    public Collection<TPlayer> getPlayers() {
        return this.players.values();
    }

    public DBCollection getCollection() {
        return collection;
    }
    public DB getDatabase() {
        return database;
    }
    public static class AuthenticationDetails {
        private String host;
        private int port;
        private String database;
        private String playerCollection;

        public AuthenticationDetails(String host, int port, String database, String playerCollection) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.playerCollection = playerCollection;
        }
        public String getPlayerCollection() {
            return playerCollection;
        }

        public String getDatabase() {
            return database;
        }
        public MongoClient getClient() throws UnknownHostException {
            return new MongoClient(host, port);
        }
    }
}
