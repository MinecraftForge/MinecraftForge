package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet20NamedEntitySpawn extends Packet
{
    /** The entity ID, in this case it's the player ID. */
    public int entityId;

    /** The player's name. */
    public String name;

    /** The player's X position. */
    public int xPosition;

    /** The player's Y position. */
    public int yPosition;

    /** The player's Z position. */
    public int zPosition;

    /** The player's rotation. */
    public byte rotation;

    /** The player's pitch. */
    public byte pitch;

    /** The current item the player is holding. */
    public int currentItem;

    public Packet20NamedEntitySpawn() {}

    public Packet20NamedEntitySpawn(EntityPlayer par1EntityPlayer)
    {
        this.entityId = par1EntityPlayer.entityId;
        this.name = par1EntityPlayer.username;
        this.xPosition = MathHelper.floor_double(par1EntityPlayer.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1EntityPlayer.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1EntityPlayer.posZ * 32.0D);
        this.rotation = (byte)((int)(par1EntityPlayer.rotationYaw * 256.0F / 360.0F));
        this.pitch = (byte)((int)(par1EntityPlayer.rotationPitch * 256.0F / 360.0F));
        ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();
        this.currentItem = var2 == null ? 0 : var2.itemID;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityId = par1DataInputStream.readInt();
        this.name = readString(par1DataInputStream, 16);
        this.xPosition = par1DataInputStream.readInt();
        this.yPosition = par1DataInputStream.readInt();
        this.zPosition = par1DataInputStream.readInt();
        this.rotation = par1DataInputStream.readByte();
        this.pitch = par1DataInputStream.readByte();
        this.currentItem = par1DataInputStream.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityId);
        writeString(this.name, par1DataOutputStream);
        par1DataOutputStream.writeInt(this.xPosition);
        par1DataOutputStream.writeInt(this.yPosition);
        par1DataOutputStream.writeInt(this.zPosition);
        par1DataOutputStream.writeByte(this.rotation);
        par1DataOutputStream.writeByte(this.pitch);
        par1DataOutputStream.writeShort(this.currentItem);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleNamedEntitySpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 28;
    }
}
