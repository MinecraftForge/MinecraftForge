package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet5PlayerInventory extends Packet
{
    /** Entity ID of the object. */
    public int entityID;

    /** Equipment slot: 0=held, 1-4=armor slot */
    public int slot;

    /** Equipped item (-1 for empty slot). */
    public int itemID;

    /** The health of the item. */
    public int itemDamage;

    public Packet5PlayerInventory() {}

    public Packet5PlayerInventory(int par1, int par2, ItemStack par3ItemStack)
    {
        this.entityID = par1;
        this.slot = par2;

        if (par3ItemStack == null)
        {
            this.itemID = -1;
            this.itemDamage = 0;
        }
        else
        {
            this.itemID = par3ItemStack.itemID;
            this.itemDamage = par3ItemStack.getItemDamage();
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.entityID = par1DataInputStream.readInt();
        this.slot = par1DataInputStream.readShort();
        this.itemID = par1DataInputStream.readShort();
        this.itemDamage = par1DataInputStream.readShort();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeInt(this.entityID);
        par1DataOutputStream.writeShort(this.slot);
        par1DataOutputStream.writeShort(this.itemID);
        par1DataOutputStream.writeShort(this.itemDamage);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handlePlayerInventory(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }
}
