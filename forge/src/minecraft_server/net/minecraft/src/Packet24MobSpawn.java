package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet24MobSpawn extends Packet
{
    /** The entity ID. */
    public int entityId;

    /** The type of mob. */
    public int type;

    /** The X position of the entity. */
    public int xPosition;

    /** The Y position of the entity. */
    public int yPosition;

    /** The Z position of the entity. */
    public int zPosition;

    /** The yaw of the entity. */
    public byte yaw;

    /** The pitch of the entity. */
    public byte pitch;
    public byte field_48113_h;

    /** Indexed metadata for Mob, terminated by 0x7F */
    private DataWatcher metaData;
    private List receivedMetadata;

    public Packet24MobSpawn() {}

    public Packet24MobSpawn(EntityLiving par1EntityLiving)
    {
        this.entityId = par1EntityLiving.entityId;
        this.type = (byte)EntityList.getEntityID(par1EntityLiving);
        this.xPosition = MathHelper.floor_double(par1EntityLiving.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1EntityLiving.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1EntityLiving.posZ * 32.0D);
        this.yaw = (byte)((int)(par1EntityLiving.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1EntityLiving.rotationPitch * 256.0F / 360.0F));
        this.field_48113_h = (byte)((int)(par1EntityLiving.prevRotationYaw2 * 256.0F / 360.0F));
        this.metaData = par1EntityLiving.getDataWatcher();
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.type = par1DataInputStream.readByte() & 255;
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.yaw = par1DataInputStream.readByte();
        this.pitch = par1DataInputStream.readByte();
        this.field_48113_h = par1DataInputStream.readByte();
        this.receivedMetadata = DataWatcher.readWatchableObjects(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeByte(this.type & 255);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeByte(this.yaw);
        par1DataOutputStream.writeByte(this.pitch);
        par1DataOutputStream.writeByte(this.field_48113_h);
        this.metaData.writeWatchableObjects(par1DataOutputStream);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleMobSpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 20;
    }
}
