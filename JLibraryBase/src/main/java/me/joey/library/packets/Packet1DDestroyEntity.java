package me.joey.library.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;

import java.util.List;

public class Packet1DDestroyEntity extends AbstractPacket {
    public static final int ID = 29;

    public Packet1DDestroyEntity() {
        super(new PacketContainer(ID), ID);
        handle.getModifier().writeDefaults();
    }

    @SuppressWarnings("unused")
    public Packet1DDestroyEntity(PacketContainer packet) {
        super(packet, ID);
    }

    /**
     * Retrieve the IDs of the entities that will be destroyed.
     *
     * @return The current entities.
     */
    @SuppressWarnings("unused")
    public List<Integer> getEntities() {
        return Ints.asList(handle.getIntegerArrays().read(0));
    }

    /**
     * Set the entities that will be destroyed.
     *
     * @param entities
     */
    public void setEntities(int[] entities) {
        handle.getIntegerArrays().write(0, entities);
    }

    /**
     * Set the entities that will be destroyed.
     *
     * @param entities
     */
    @SuppressWarnings("unused")
    public void setEntities(List<Integer> entities) {
        setEntities(Ints.toArray(entities));
    }
}
