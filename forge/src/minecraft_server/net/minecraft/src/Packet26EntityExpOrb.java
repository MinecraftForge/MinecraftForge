package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet26EntityExpOrb extends Packet
{
    /** Entity ID for the XP Orb */
    public int entityId;
    public int posX;
    public int posY;
    public int posZ;

    /** The Orbs Experience points value. */
    public int xpValue;

    public Packet26EntityExpOrb() {}

    public Packet26EntityExpOrb(EntityXPOrb par1EntityXPOrb)
    {
        this.entityId = par1EntityXPOrb.entityId;
        this.posX = MathHelper.floor_double(par1EntityXPOrb.posX * 32.0D);
        this.posY = MathHelper.floor_double(par1EntityXPOrb.posY * 32.0D);
        this.posZ = MathHelper.floor_double(par1EntityXPOrb.posZ * 32.0D);
        this.xpValue = par1EntityXPOrb.getXpValue();
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.posX = par1DataInputStream.readInt();
        this.posY = par1DataInputStream.readInt();
        this.posZ = par1DataInputStream.readInt();
        this.xpValue = par1DataInputStream.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeInt(this.posX);
        par1DataOutputStream.writeInt(this.posY);
        par1DataOutputStream.writeInt(this.posZ);
        par1DataOutputStream.writeShort(this.xpValue);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityExpOrb(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 18;
    }
}
