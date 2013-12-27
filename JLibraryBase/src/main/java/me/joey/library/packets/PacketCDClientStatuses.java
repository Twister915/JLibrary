package me.joey.library.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;

/**
 * Sent when the client is ready to complete login and when the client is ready to respawn after death.
 *
 * @author Kristian
 */
public class PacketCDClientStatuses extends AbstractPacket {
    public static final int ID = 205;

    /**
     * Enumeration of all the known payload values.
     *
     * @author Kristian
     */
    public static class Payloads extends IntEnum {
        @SuppressWarnings("unused")
        public static final int INITIAL_SPAWN = 0;
        public static final int RESPAWN_AFTER_DEATH = 1;

        private static final Payloads INSTANCE = new Payloads();

        public static Payloads getInstance() {
            return INSTANCE;
        }
    }

    public PacketCDClientStatuses() {
        super(new PacketContainer(ID), ID);
        handle.getModifier().writeDefaults();
    }

    @SuppressWarnings("unused")
    public PacketCDClientStatuses(PacketContainer packet) {
        super(packet, ID);
    }

    /**
     * Retrieve whether or not we're logging in or respawning.
     *
     * @return The current Payload
     * @see {@link Payloads}.
     */
    @SuppressWarnings("unused")
    public int getPayload() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set whether or not we're logging in or respawning.
     *
     * @param value - new value.
     * @see {@link Payloads}.
     */
    public void setPayload(int value) {
        handle.getIntegers().write(0, value);
    }
}
