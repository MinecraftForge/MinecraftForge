package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet34EntityTeleport extends Packet
{
    /** ID of the entity. */
    public int entityId;

    /** X position of the entity. */
    public int xPosition;

    /** Y position of the entity. */
    public int yPosition;

    /** Z position of the entity. */
    public int zPosition;

    /** Yaw of the entity. */
    public byte yaw;

    /** Pitch of the entity. */
    public byte pitch;

    public Packet34EntityTeleport() {}

    public Packet34EntityTeleport(Entity par1Entity)
    {
        this.entityId = par1Entity.entityId;
        this.xPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.yaw = (byte)((int)(par1Entity.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1Entity.rotationPitch * 256.0F / 360.0F));
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.yaw = (byte)par1DataInputStream.read();
        this.pitch = (byte)par1DataInputStream.read();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.write(this.yaw);
        par1DataOutputStream.write(this.pitch);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntityTeleport(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 34;
    }
}
