package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet41EntityEffect extends Packet
{
    public int entityId;
    public byte effectId;

    /** effectAmp */
    public byte effectAmp;
    public short duration;

    public Packet41EntityEffect() {}

    public Packet41EntityEffect(int par1, PotionEffect par2PotionEffect)
    {
        this.entityId = par1;
        this.effectId = (byte)(par2PotionEffect.getPotionID() & 255);
        this.effectAmp = (byte)(par2PotionEffect.getAmplifier() & 255);
        this.duration = (short)par2PotionEffect.getDuration();
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.effectId = par1DataInputStream.readByte();
        this.effectAmp = par1DataInputStream.readByte();
        this.duration = par1DataInputStream.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeByte(this.effectId);
        par1DataOutputStream.writeByte(this.effectAmp);
        par1DataOutputStream.writeShort(this.duration);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityEffect(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }
}
