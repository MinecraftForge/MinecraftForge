package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet104WindowItems extends Packet
{
    /**
     * The id of window which items are being sent for. 0 for player inventory.
     */
    public int windowId;

    /** Stack of items */
    public ItemStack[] itemStack;

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.windowId = par1DataInputStream.readByte();
        short var2 = par1DataInputStream.readShort();
        this.itemStack = new ItemStack[var2];

        for (int var3 = 0; var3 < var2; ++var3)
        {
            this.itemStack[var3] = this.readItemStack(par1DataInputStream);
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte(this.windowId);
        par1DataOutputStream.writeShort(this.itemStack.length);

        for (int var2 = 0; var2 < this.itemStack.length; ++var2)
        {
            this.writeItemStack(this.itemStack[var2], par1DataOutputStream);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleWindowItems(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 3 + this.itemStack.length * 5;
    }
}
