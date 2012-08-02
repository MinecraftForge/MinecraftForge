package net.minecraft.src.forge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A interface for Entities that need extra information to be communicated
 * between the server and client when they are spawned.
 */
public interface ISpawnHandler
{
    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param data The packet data stream
     */
    public void writeSpawnData(DataOutputStream data) throws IOException;

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param data The packet data stream
     */
    public void readSpawnData(DataInputStream data) throws IOException;
}
