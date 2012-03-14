package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet102WindowClick extends Packet
{
    /** The id of the window which was clicked. 0 for player inventory. */
    public int window_Id;

    /** The clicked slot (-999 is outside of inventory) */
    public int inventorySlot;

    /** 1 when right-clicking and otherwise 0 */
    public int mouseClick;

    /** A unique number for the action, used for transaction handling */
    public short action;

    /** Item stack for inventory */
    public ItemStack itemStack;
    public boolean holdingShift;

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleWindowClick(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.window_Id = par1DataInputStream.readByte();
        this.inventorySlot = par1DataInputStream.readShort();
        this.mouseClick = par1DataInputStream.readByte();
        this.action = par1DataInputStream.readShort();
        this.holdingShift = par1DataInputStream.readBoolean();
        this.itemStack = this.readItemStack(par1DataInputStream);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte(this.window_Id);
        par1DataOutputStream.writeShort(this.inventorySlot);
        par1DataOutputStream.writeByte(this.mouseClick);
        par1DataOutputStream.writeShort(this.action);
        par1DataOutputStream.writeBoolean(this.holdingShift);
        this.writeItemStack(this.itemStack, par1DataOutputStream);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 11;
    }
}
