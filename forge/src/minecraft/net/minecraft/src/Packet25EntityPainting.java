package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet25EntityPainting extends Packet
{
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int direction;
    public String title;

    public Packet25EntityPainting() {}

    public Packet25EntityPainting(EntityPainting par1EntityPainting)
    {
        this.entityId = par1EntityPainting.entityId;
        this.xPosition = par1EntityPainting.xPosition;
        this.yPosition = par1EntityPainting.yPosition;
        this.zPosition = par1EntityPainting.zPosition;
        this.direction = par1EntityPainting.direction;
        this.title = par1EntityPainting.art.title;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.title = readString(par1DataInputStream, EnumArt.maxArtTitleLength);
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.direction = par1DataInputStream.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        writeString(this.title, par1DataOutputStream);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeInt(this.direction);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityPainting(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 24;
    }
}
