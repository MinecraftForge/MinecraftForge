package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet40EntityMetadata extends Packet
{
    public int entityId;
    private List metadata;

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.metadata = DataWatcher.readWatchableObjects(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        DataWatcher.writeObjectsInListToStream(this.metadata, par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityMetadata(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 5;
    }

    public List getMetadata()
    {
        return this.metadata;
    }
}
