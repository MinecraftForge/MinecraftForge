package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet21PickupSpawn extends Packet
{
    /** Unique entity ID. */
    public int entityId;

    /** The item X position. */
    public int xPosition;

    /** The item Y position. */
    public int yPosition;

    /** The item Z position. */
    public int zPosition;

    /** The item rotation. */
    public byte rotation;

    /** The item pitch. */
    public byte pitch;

    /** The item roll. */
    public byte roll;
    public int itemID;

    /** The number of items. */
    public int count;

    /** The health of the item. */
    public int itemDamage;

    public Packet21PickupSpawn() {}

    public Packet21PickupSpawn(EntityItem par1EntityItem)
    {
        this.entityId = par1EntityItem.entityId;
        this.itemID = par1EntityItem.item.itemID;
        this.count = par1EntityItem.item.stackSize;
        this.itemDamage = par1EntityItem.item.getItemDamage();
        this.xPosition = MathHelper.floor_double(par1EntityItem.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1EntityItem.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1EntityItem.posZ * 32.0D);
        this.rotation = (byte)((int)(par1EntityItem.motionX * 128.0D));
        this.pitch = (byte)((int)(par1EntityItem.motionY * 128.0D));
        this.roll = (byte)((int)(par1EntityItem.motionZ * 128.0D));
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.itemID = par1DataInputStream.readShort();
        this.count = par1DataInputStream.readByte();
        this.itemDamage = par1DataInputStream.readShort();
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.rotation = par1DataInputStream.readByte();
        this.pitch = par1DataInputStream.readByte();
        this.roll = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        par1DataOutputStream.writeShort(this.itemID);
        par1DataOutputStream.writeByte(this.count);
        par1DataOutputStream.writeShort(this.itemDamage);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeByte(this.rotation);
        par1DataOutputStream.writeByte(this.pitch);
        par1DataOutputStream.writeByte(this.roll);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handlePickupSpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 24;
    }
}
