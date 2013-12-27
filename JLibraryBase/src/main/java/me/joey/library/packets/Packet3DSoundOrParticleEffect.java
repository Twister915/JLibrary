package me.joey.library.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;
import org.bukkit.Location;
import org.bukkit.World;

public class Packet3DSoundOrParticleEffect extends AbstractPacket {
    public static final int ID = 61;

    /**
     * Enumeration of all the sound effects this packet can send.
     *
     * @author Kristian
     */
    public static class SoundEffects extends IntEnum {
        @SuppressWarnings("unused")
        public static final int RANDOM_CLICK_1 = 1000;
        @SuppressWarnings("unused")
        public static final int RANDOM_CLICK_2 = 1001;
        @SuppressWarnings("unused")
        public static final int RANDOM_BOW = 1002;
        @SuppressWarnings("unused")
        public static final int RANDOM_DOOR = 1003;
        @SuppressWarnings("unused")
        public static final int RANDOM_FIZZ = 1004;
        /**
         * Data ID: Record ID
         */
        @SuppressWarnings("unused")
        public static final int PLAY_MUSIC_DISK = 1005;
        @SuppressWarnings("unused")
        public static final int MOB_GHAST_CHARGE = 1007;
        /**
         * The sound of {@link #MOB_GHAST_FIREBALL}, but with a lower volume.
         */
        @SuppressWarnings("unused")
        public static final int MOB_GHAST_FIREBALL_QUIET = 1009;
        @SuppressWarnings("unused")
        public static final int MOB_GHAST_FIREBALL = 1008;
        @SuppressWarnings("unused")
        public static final int MOB_ZOMBIE_WOOD = 1010;
        @SuppressWarnings("unused")
        public static final int MOB_ZOMBIE_METAL = 1011;
        @SuppressWarnings("unused")
        public static final int MOB_ZOMBIE_WOODBREAK = 1012;
        @SuppressWarnings("unused")
        public static final int MOB_WITHER_SPAWN = 1013;
        @SuppressWarnings("unused")
        public static final int MOB_WITHER_SHOOT = 1014;
        @SuppressWarnings("unused")
        public static final int MOB_BAT_TAKEOFF = 1015;
        @SuppressWarnings("unused")
        public static final int MOB_ZOMBIE_INFECT = 1016;
        @SuppressWarnings("unused")
        public static final int MOB_ZOMBIE_UNFECT = 1017;
        @SuppressWarnings("unused")
        public static final int MOB_ENDER_DRAGON_END = 1018;
        @SuppressWarnings("unused")
        public static final int RANDOM_ANVIL_BREAK = 1020;
        @SuppressWarnings("unused")
        public static final int RANDOM_ANVIL_USE = 1021;
        @SuppressWarnings("unused")
        public static final int RANDOM_ANVIL_LAND = 1022;

        private static final SoundEffects INSTANCE = new SoundEffects();

        /**
         * Retrieve an instance of the sound effects enum.
         *
         * @return Sound effects enum.
         */
        public static SoundEffects getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Enumeration of all the particle effects this packet can send.
     *
     * @author Kristian
     */
    public static class ParticleEffects extends IntEnum {
        /**
         * Spawn 10 smoke particles.
         * <p/>
         * Data: Direction
         */
        @SuppressWarnings("unused")
        public static final int SPAWN_SMOKE_PARTICLES = 2000;
        /**
         * Block break particles.
         * <p/>
         * Data: Block ID.
         */
        public static final int BLOCK_BREAK = 2001;
        /**
         * Splash potion particles.
         * <p/>
         * Data: Potion ID (damage value)
         */
        @SuppressWarnings("unused")
        public static final int SPLASH_POTION = 2002;
        @SuppressWarnings("unused")
        public static final int EYE_OF_ENDER = 2003;

        /**
         * Mob spawn. Smoke and flames.
         */
        @SuppressWarnings("unused")
        public static final int MOB_SPAWN_EFFECT = 2004;

        /**
         * Spawn a "happy villager" effect (hearts).
         */
        @SuppressWarnings("unused")
        public static final int HAPPY_VILLAGER = 2005;

        private static final ParticleEffects INSTANCE = new ParticleEffects();

        /**
         * Retrieve an instance of the particle effects enum.
         *
         * @return Particle effects enum.
         */
        public static ParticleEffects getInstance() {
            return INSTANCE;
        }
    }

    public static class SmokeDirections extends IntEnum {
        @SuppressWarnings("unused")
        public static final int SOUTH_EAST = 0;
        @SuppressWarnings("unused")
        public static final int SOUTH = 1;
        @SuppressWarnings("unused")
        public static final int SOUTH_WEST = 2;
        @SuppressWarnings("unused")
        public static final int EAST = 3;
        public static final int UP = 4;
        @SuppressWarnings("unused")
        public static final int WEST = 5;
        @SuppressWarnings("unused")
        public static final int NORTH_EAST = 6;
        @SuppressWarnings("unused")
        public static final int NORTH = 7;
        @SuppressWarnings("unused")
        public static final int NORTH_WEST = 8;

        private static final SmokeDirections INSTANCE = new SmokeDirections();

        /**
         * Retrieve an instance of the smoke direction enum.
         *
         * @return Smoke direction enum.
         */
        public static SmokeDirections getInstance() {
            return INSTANCE;
        }
    }

    public Packet3DSoundOrParticleEffect() {
        super(new PacketContainer(ID), ID);
        handle.getModifier().writeDefaults();
    }

    @SuppressWarnings("unused")
    public Packet3DSoundOrParticleEffect(PacketContainer packet) {
        super(packet, ID);
    }

    /**
     * Retrieve the ID of the effect.
     *
     * @return The current Effect ID
     * @see {@link SoundEffects} and {@link ParticleEffects}.
     */
    @SuppressWarnings("unused")
    public int getEffectId() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set the ID of the effect.
     *
     * @param value - new value.
     * @see {@link SoundEffects} and {@link ParticleEffects}.
     */
    public void setEffectId(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the X location of the effect..
     *
     * @return The current X
     */
    public int getX() {
        return handle.getIntegers().read(2);
    }

    /**
     * Set the X location of the effect..
     *
     * @param value - new value.
     */
    public void setX(int value) {
        handle.getIntegers().write(2, value);
    }

    /**
     * Retrieve the Y location of the effect..
     *
     * @return The current Y
     */
    public int getY() {
        return handle.getIntegers().read(3);
    }

    /**
     * Set the Y location of the effect..
     *
     * @param value - new value.
     */
    public void setY(int value) {
        handle.getIntegers().write(3, value);
    }

    /**
     * Retrieve the Z location of the effect..
     *
     * @return The current Z
     */
    public int getZ() {
        return handle.getIntegers().read(4);
    }

    /**
     * Set the Z location of the effect..
     *
     * @param value - new value.
     */
    public void setZ(int value) {
        handle.getIntegers().write(4, value);
    }

    /**
     * Retrieve the location of this particle or sound effect.
     *
     * @param world - the containing world.
     * @return The location.
     */
    public Location getLocation(World world) {
        return new Location(world, getX(), getY(), getZ());
    }

    /**
     * Set the location of this particle or sound effect.
     *
     * @param loc - the location.
     */
    public void setLocation(Location loc) {
        setX(loc.getBlockX());
        setY(loc.getBlockY());
        setZ(loc.getBlockZ());
    }

    /**
     * Retrieve extra data for certain effects.
     *
     * @return The current Data
     * @see {@link SmokeDirections}
     */
    public int getData() {
        return handle.getIntegers().read(1);
    }

    /**
     * Set extra data for certain effects.
     *
     * @param value - new value.
     * @see {@link SmokeDirections}
     */
    public void setData(int value) {
        handle.getIntegers().write(1, value);
    }

    /**
     * Retrieve whether or not to ignore the distance of a sound.
     * <p/>
     * If TRUE, the effect will always be played 2 blocks away in the correct direction.
     *
     * @return The current Disable relative volume
     */
    @SuppressWarnings("unused")
    public boolean getDisableRelativeVolume() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }

    /**
     * Set whether or not to ignore the distance of a sound.
     * <p/>
     * If TRUE, the effect will always be played 2 blocks away in the correct direction.
     *
     * @param value - TRUE to disable distance, FALSE otherwise.
     */
    public void setDisableRelativeVolume(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
}

