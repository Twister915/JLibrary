package me.joey.library.player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.joey.library.TPlugin;
import me.joey.library.packets.Packet37BlockBreakAnimation;
import me.joey.library.packets.Packet3DSoundOrParticleEffect;
import me.joey.library.packets.Packet3FParticle;
import me.joey.library.packets.PacketCDClientStatuses;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;


import static me.joey.library.packets.Packet3DSoundOrParticleEffect.ParticleEffects.BLOCK_BREAK;
import static me.joey.library.packets.PacketCDClientStatuses.Payloads.RESPAWN_AFTER_DEATH;
/**
 * TPlayer is a representation of the player in Bukkit that can store more data about the player, and also execute methods
 * on the player that are considered "helper" methods. These methods are utilities to preform very simple tasks that would
 * otherwise be more difficult using the vanilla Bukkit API.
 */
@SuppressWarnings("UnusedDeclaration")
@EqualsAndHashCode(of = {"playerName", "timeJoined"}, doNotUseGetters = true)
@ToString(of = {"playerName", "timeJoined"}, includeFieldNames = true, doNotUseGetters = true)
public class TPlayer {
    /**
     * The variable storing the actual player this represents. R/O
     */
    @Getter private String playerName;
    /**
     * The database document representing the player.
     */
    private DBObject playerDocument;
    /**
     * The time the player joined.
     */
    @Getter private long timeJoined;
    /**
     * The time the player has spent online.
     */
    private long timeOnline;
    /**
     * A boolean representing that a player has joined for the first time.
     */
    @Getter private boolean firstJoin;
    /**
     * Scoreboard object for the player
     */
    private Scoreboard scoreboard;
    /**
     * Objective for player on sidebar
     */
    private Objective sidebar = null;

    private TPlugin plugin;
    /**
     * This is a protected method for creating a TPlayer instance from a Bukkit player.
     * @param player The Bukkit player this represents.
     */
    protected TPlayer(Player player, TPlugin plugin) {
        this.plugin = plugin;
        this.playerName = player.getName();
        this.timeJoined = Calendar.getInstance().getTimeInMillis();
        if (TPlayerManager.getInstance().getCollection() == null) {
            return;
        }
        this.playerDocument = TPlayer.getPlayerObject(player.getName());
        if (this.playerDocument == null) {
            this.playerDocument = new BasicDBObject("username", player.getName()); //So we didn't find it, create our own, and set the username var.
            this.playerDocument.put("time-online", 0l); //Sets the online time to 0 so this var is present (long).
            this.firstJoin = true;
        }
        this.playerDocument.put("last-seen", Calendar.getInstance().getTimeInMillis()); //Update last-seen
        this.playerDocument.put("online", true); //Update the online variable
        this.save();
        this.timeOnline = (Long) this.playerDocument.get("time-online");
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        //this.getPlayer().setScoreboard(this.scoreboard);
    }

    public static DBObject getPlayerObject(String name) {
        BasicDBObject query = new BasicDBObject("username", name); //Query the database for the player
        DBCursor cursor = TPlayerManager.getInstance().getCollection().find(query);
        if (cursor.hasNext()) return cursor.next();
        else return null;
    }

    /**
     * Get the actual player this object represents
     * @return The player object from Bukkit that this represents
     */
    public Player getPlayer() {
        return Bukkit.getPlayerExact(this.playerName);
    }

    /**
     * Plays a sound for the player at a volume with no pitch modification.
     * @param sound The sound.
     * @param volume The volume.
     */
    public void playSound(Sound sound, int volume) {
        this.playSound(sound, volume, 0);
    }

    /**
     * Plays a sound at the volume of 10
     * @param sound The sound.
     */
    public void playSound(Sound sound) {
        this.playSound(sound, 10);
    }

    /**
     * Plays a sound at a volume with a specific pitch.
     * @param sound The sound.
     * @param volume The volume.
     * @param pitch The pitch.
     */
    public void playSound(Sound sound, int volume, int pitch) {
        this.getPlayer().playSound(getPlayer().getLocation(), sound, volume, pitch);
    }

    /**
     * Adds a potion effect to a player quickly.
     * @param type The potion effect type
     * @param length The length of the potion effect.
     * @param intensity The intensity of the potion effect.
     * @param ambient Is this potion effect ambient? Check Bukkit docs for more info on this one.
     */
    public void addPotionEffect(PotionEffectType type, int length, int intensity, boolean ambient) {
        PotionEffect toAdd = new PotionEffect(type, (length == Integer.MAX_VALUE ? Integer.MAX_VALUE : length*20), intensity, ambient);
        this.getPlayer().addPotionEffect(toAdd);
    }

    /**
     * Adds a potion effect to a player quickly. Has a default ambiance of "true".
     * @param type The potion effect type
     * @param length The length (I believe in ticks).
     * @param intensity The intensity of the potion effect.
     */
    public void addPotionEffect(PotionEffectType type, int length, int intensity) {
        this.addPotionEffect(type,length,intensity,true);
    }

    /**
     * Adds a potion effect to a player quickly. Has a default ambiance of "true", and a default intensity of 0.
     * @param type The potion effect type.
     * @param length The length (I believe in ticks).
     */
    public void addPotionEffect(PotionEffectType type, int length) {
        this.addPotionEffect(type, length, 0);
    }

    /**
     * Adds a potion effect to a player quickly. Adds the potion effect forever, with an ambiance of "true", and an intensity of 0.
     * @param type The type of the potion effect.
     */
    public void addPotionEffect(PotionEffectType type) {
        this.addPotionEffect(type, Integer.MAX_VALUE);
    }

    /**
     * Adds a potion effect to a player quickly, and forever. This allows you to specify an intensity.
     * @param type The type of the potion effect.
     * @param intensity The intensity of the potion effect. (0 = Level 1)
     */
    public void addInfinitePotionEffect(PotionEffectType type, int intensity) {
        this.addPotionEffect(type, Integer.MAX_VALUE, intensity);
    }

    /**
     * This will remove ALL active potion effects except those specified.
     * @param exclusions Specify some potion effect types, or none, and they will not be removed.
     */
    public void removeAllPotionEffects(PotionEffectType... exclusions) {
        List<PotionEffectType> doNotRemove = Arrays.asList(exclusions);
        for (PotionEffect effect : this.getPlayer().getActivePotionEffects()) {
            if (doNotRemove.contains(effect.getType())) continue;
            this.getPlayer().removePotionEffect(effect.getType());
        }
    }

    /**
     * Remove specified potion effects
     * @param potionEffects Specify the potion effect types to remove. This method will not run if none are specified
     */
    public void removePotionEffects(PotionEffectType... potionEffects) {
        if (potionEffects.length < 1) return;
        List<PotionEffectType> potionEffectTypes = Arrays.asList(potionEffects);
        for (PotionEffect effect : this.getPlayer().getActivePotionEffects()) {
            if (potionEffectTypes.contains(effect.getType())) this.getPlayer().removePotionEffect(effect.getType());
        }
    }

    public Integer getCurrentPotionLevel(PotionEffectType effectType) {
        Integer level = -1;
        for (PotionEffect effect : this.getPlayer().getActivePotionEffects()) {
            if (!effect.getType().equals(effectType)) continue;
            level = effect.getAmplifier();
            break;
        }
        return level;
    }

    /**
     * Test is player has certain potion effect
     * @param e ~ Potion Type
     * @return True if the player does
     */
    public boolean hasPotionEffect(PotionEffectType e) {
        return e != null && getPlayer().hasPotionEffect(e);

    }

    /**
     * Give an item to a player
     * @param type The material of the item
     * @param quantity The quantity of the item
     * @param data_value The data value (used for wool colors, etc)
     * @param title The title of the item.
     * @param lore The lore of the item
     * @param slot The slot to put the item in
     */
    public ItemStack giveItem(Material type, int quantity, short data_value, String title, String[] lore, int slot) {
        if (type == null) return null;
        if (quantity < 1) return null;
        ItemStack itemStack = new ItemStack(type, quantity);
        if (data_value > 1) itemStack.setDurability(data_value);
        ItemMeta meta = itemStack.getItemMeta();
        if (title != null) meta.setDisplayName(title);
        if (lore != null) meta.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(meta);
        //HotBar slots are from 1-9
        if (slot < 1 || slot > 9) {
            int toGive = quantity;
            while (toGive > 0) {
                itemStack.setAmount(Math.min(itemStack.getMaxStackSize(), toGive));
                this.getPlayer().getInventory().addItem(itemStack);
                toGive  = toGive-itemStack.getAmount();
            }
        }
        else this.getPlayer().getInventory().setItem(slot-1, itemStack);
        return itemStack;
    }
    /**
     * Give an item to a player
     * @param type The material of the item
     * @param quantity The quantity of the item
     * @param data_value The data value (used for wool colors, etc)
     * @param title The title of the item.
     * @param lore The lore of the item
     */
    public ItemStack giveItem(Material type, int quantity, short data_value, String title, String[] lore) {
        return giveItem(type, quantity, data_value, title, lore, -1);
    }
    /**
     * Give an item to a player
     * @param type The material of the item
     * @param quantity The quantity of the item
     * @param data_value The data value (used for wool colors, etc)
     * @param title The title of the item.
     */
    public ItemStack giveItem(Material type, int quantity, short data_value, String title) {
        return giveItem(type, quantity, data_value, title, null);
    }

    /**
     * Give an item to a player
     * @param type The material of the item
     * @param quantity The quantity of the item
     * @param data_value The data value (used for wool colors, etc)
     */
    public ItemStack giveItem(Material type, int quantity, short data_value) {
        return giveItem(type, quantity, data_value, null);
    }
    /**
     * Give an item to a player
     * @param type The material of the item
     * @param quantity The quantity of the item

     */
    public ItemStack giveItem(Material type, int quantity) {
        return giveItem(type, quantity, (short)0);
    }
    /**
     * Gives a single item to a player
     * @param type The material of the item
     */
    public ItemStack giveItem(Material type) {
        return giveItem(type, 1);
    }

    /**
     *
     * @param material The material of the item to search for
     * @param quantity How many of the items to remove
     * @return If the item was removed (if the user had enough).
     */
    public boolean removeItem(Material material, int quantity) {
        if (!getPlayer().getInventory().contains(material, quantity)) return false;
        getPlayer().getInventory().removeItem(new ItemStack(material, quantity));
        return true;
    }

    /**
     * Removes a singular item from the player's inventory
     * @param material The material of the item to remove.
     * @return If the item was removed (ie; if they had the item in their inventory)
     */
    public boolean removeItem(Material material) {
        return removeItem(material, 1);
    }

    /**
     * Kills the player.
     */
    public void kill() {
        this.getPlayer().damage(this.getPlayer().getHealth());
    }

    /**
     * Sends messages to the player
     * @param message The message(s) to send to the player.
     */
    public void sendMessage(String... message) {
        if (!this.isOnline()) return;
        for (String m : message) {
            this.getPlayer().sendMessage(m);
        }
    }

    /**
     * Gets the MongoDB object that stores data about the player.
     * @return The MongoDB Document object that you can use to store raw data about the player. Please be careful
     * when using this because it's very easy to mess things up with it.
     */
    public DBObject getPlayerDocument() {
        return playerDocument;
    }

    /**
     * Called by the TPlayerManager when the player disconnects. Do not call otherwise
     */
    void disconnected() {
        this.getPlayerDocument().put("online", false);
        Object o = this.getPlayerDocument().get("time-online");
        if (o == null) o = 0l;
        if (!(o instanceof Long)) return;
        long timeOnline = (Long)o;
        long now = Calendar.getInstance().getTimeInMillis();
        timeOnline = timeOnline+(now-timeJoined);
        this.getPlayerDocument().put("time-online", timeOnline);
        this.getPlayerDocument().put("last-seen", now);
        this.save();
    }

    /**
     * Saves the player document to the database. :D
     */
    public void save() {
        TPlayerManager.getInstance().getCollection().save(this.playerDocument);
    }

    /**
     * Use this to store prefixed and managed data about a player that can be accessed later.
     * @param plugin The plugin responsible for storing the data.
     * @param storable The storable object.
     */
    public void store(TPlugin plugin, TPlayerStorable storable) {
        this.playerDocument.put(TPlayer.formatStorable(plugin.getStorablePrefix(), storable.getName()), storable.getValue());
        this.save();
    }

    /**
     * Gets the value of a storable
     * @param plugin The plugin storing this data. Used for keys.
     * @param storable An empty storable object with the proper data name.
     * @return The stored object.
     */
    public Object getStorable(TPlugin plugin, TPlayerStorable storable) {
        return this.getStorable(plugin, storable.getName());
    }
    /**
     * Gets the value of a storable
     * @param plugin The plugin storing this data. Used for keys.
     * @param storable_key The storable key.
     * @return The stored object.
     */
    public Object getStorable(TPlugin plugin, String storable_key) {
        return this.playerDocument.get(TPlayer.formatStorable(plugin.getStorablePrefix(), storable_key));
    }

    /**
     * Formats strings for a storable key.
     * @param prefix The prefix of the storable (from the plugin)
     * @param name The name of the storable
     * @return Fully formatted storable key.
     */
    public static String formatStorable(String prefix, String name) {
        return prefix + "_" + name;
    }

    /**
     * Checks if there is another object matching a storable in the database
     * @param storable The storable prefix of the object
     * @param value The value to match
     * @return If there is a match!
     */
    public static boolean anyMatchesToStorable(TPlugin plugin, String storable, Object value) {
        DBObject object = new BasicDBObject(TPlayer.formatStorable(plugin.getStorablePrefix(), storable), value);
        DBCursor cursor = TPlayerManager.getInstance().getCollection().find(object);
        return cursor.hasNext();
    }

    /**
     * Gets the amount of time spent online by this player (accurate at time being called)
     * @return The time online in milliseconds.
     */
    public long getTimeOnline() {
        return timeOnline+(Calendar.getInstance().getTimeInMillis()-this.timeJoined);
    }

    /**
     * Plays a particle effect for the user
     * @param effect The effect to play
     * @throws Exception
     */
    public void playParticleEffect(TParticleEffect effect) throws Exception {
        for (Packet3FParticle.ParticleEffect type : effect.getParticleEffectType()) {
            Packet3FParticle packet = new Packet3FParticle();
            packet.setParticleName(type.getParticleName());
            packet.setX((float) effect.getLocation().getX());
            packet.setY((float) effect.getLocation().getY());
            packet.setZ((float) effect.getLocation().getZ());
            packet.setOffsetX(effect.getOffset());
            packet.setOffsetY(effect.getHeight());
            packet.setOffsetZ(effect.getOffset());
            packet.setParticleSpeed(effect.getSpeed());
            packet.setNumberOfParticles(effect.getCount());

            sendPacket(packet.getHandle());
        }
    }

    /**
     * Should possibly fake a break block animation
     * @param block The block to pretend to break
     * @throws Exception
     */
    private void fakeBlockBreak(Block block) throws Exception {
        Packet37BlockBreakAnimation packet = new Packet37BlockBreakAnimation();
        packet.setEntityID(this.getPlayer().getEntityId());
        packet.setX(block.getLocation().getBlockX());
        packet.setY(block.getLocation().getBlockY());
        packet.setZ(block.getLocation().getBlockZ());
        packet.setDestroyStage(7);
        sendPacket(packet.getHandle());
    }

    /**
     * Teleports the player to a location with a nice Enderman_Teleport sound.
     * @param location The location to teleport the player to
     */
    public void teleport(Location location) {
        this.playSound(Sound.ENDERMAN_TELEPORT);
        this.getPlayer().teleport(location);
    }

    /**
     * Clears the inventory of the player.
     */
    public void clearInventory() {
        this.getPlayer().getInventory().clear();
        this.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        }

    /**
     * Used to reset everything about the player, can be fine tuned the reset params.
     */
    public void resetPlayer(PlayerResetParams params) {
        if (!isOnline()) return;
        if (params == null) {
            params = new PlayerResetParams();
        }
        if (params.isClearXP()) {
            getPlayer().setExp(0);
            getPlayer().setLevel(0);
            getPlayer().setTotalExperience(0);
        }
        if (params.isClearPotions()) {
            removeAllPotionEffects();
        }
        if (params.isResetInventory()) {
            clearInventory();
        }
        if (params.isRestoreHealth()) {
            getPlayer().setHealth(getPlayer().getMaxHealth());
            getPlayer().setRemainingAir(20);
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                getPlayer().setFireTicks(0);
            }
        }, 2L);
        if (params.isRestoreFood()) {
            getPlayer().setFoodLevel(20);
            getPlayer().setExhaustion(0);
        }
        getPlayer().setSneaking(false);
        if (!params.isResetFlight()) return;
        /*if (params.isMovePlayerDown()) {
            if (getPlayer().getAllowFlight() && getPlayer().isFlying() && getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                Block block = getPlayer().getLocation().getBlock();
                while (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                getPlayer().teleport(block.getLocation());
            }
        }*/
        getPlayer().setVelocity(new Vector(0,0,0));
        getPlayer().setFallDistance(0F);
        getPlayer().setAllowFlight(false);
    }

    /**
     * No params reset.
     */
    public void resetPlayer() {
        this.resetPlayer(null);
    }

    /**
     * Resets the Scoreboard
     */
    public void resetScoreboard() {
        if (!this.isOnline()) return;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.sidebar = null;
        this.getPlayer().setScoreboard(this.scoreboard);
    }

    /**
     * Block break animation
     * @param location The location to put the animation at
     * @param material The material to simulate breaking.
     */
    public void playBlockBreak(Location location, Material material) {
        if (!material.isBlock())return;
        Packet3DSoundOrParticleEffect packet = new Packet3DSoundOrParticleEffect();
        packet.setX(location.getBlockX());
        packet.setY(location.getBlockY());
        packet.setZ(location.getBlockZ());
        packet.setEffectId(BLOCK_BREAK);
        packet.setData(material.getId());
        packet.setDisableRelativeVolume(false);

        sendPacket(packet.getHandle());
    }

    /**
     * Forces a respawn
     */
    public void respawn() {
        PacketCDClientStatuses packet = new PacketCDClientStatuses();
        packet.setPayload(RESPAWN_AFTER_DEATH);
        sendClientPacket(packet.getHandle());
    }

    public void setScoreboardSideTitle(String title) {
        if (!this.isOnline()) return;
        if (this.sidebar == null) {
            String s = new BigInteger(13, this.plugin.getRandom()).toString(5);
            this.sidebar = this.scoreboard.registerNewObjective(s.substring(0, Math.min(s.length(), 15)), "dummy");
            this.sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        this.sidebar.setDisplayName(title);
    }

    public void setScoreBoardSide(String key, int value) {
        if (!this.isOnline()) return;
        Score score = this.sidebar.getScore(Bukkit.getOfflinePlayer(key.substring(0, Math.min(key.length(), 15))));
        score.setScore(value);
        if(getPlayer() == null) return;
        if(!getPlayer().isOnline()) return;
        getPlayer().setScoreboard(this.scoreboard);
    }

    public static class TParticleEffect {
        @Getter private Location location;
        @Getter private float height;
        @Getter private float offset;
        @Getter private int count;
        @Getter private float speed;
        private List<Packet3FParticle.ParticleEffect> particleEffectTypes;
        public TParticleEffect(Location location, float height, float offset, int count, float speed, Packet3FParticle.ParticleEffect... particleEffectType) {
            this.location = location;
            this.height = height;
            this.offset = offset;
            this.count = count;
            this.speed = speed;
            this.particleEffectTypes = Arrays.asList(particleEffectType);
        }

        public List<Packet3FParticle.ParticleEffect> getParticleEffectType() {
            return particleEffectTypes;
        }

    }

    public void sendPacket(PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), packet);
        } catch (InvocationTargetException e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot send " + packet + " to " + getPlayer(), e);
        }
    }

    public void sendClientPacket(PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(getPlayer(), packet);
        } catch (InvocationTargetException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot send " + packet + " to " + getPlayer(), e);
        }
    }

    public boolean isOnline() {
        return Bukkit.getOfflinePlayer(this.playerName).isOnline() && getPlayer() != null;
    }

    /**
     * Returns the ping asynchronously via the PingCallbackEventHandler you pass in
     * In that event handler you can do getPing() to the passed in PingCallbackEvent;
     * @param eventHandler ~ The PingCallbackEventHandler
     * @see me.joey.library.player.IPUtils.PingCallbackEvent
     * @see me.joey.library.player.IPUtils.PingCallbackEventHandler
     */
    public void getPing(IPUtils.PingCallbackEventHandler eventHandler) {
        IPUtils.getPing(this.plugin, getPlayer().getAddress().getAddress(), eventHandler);
    }
}
