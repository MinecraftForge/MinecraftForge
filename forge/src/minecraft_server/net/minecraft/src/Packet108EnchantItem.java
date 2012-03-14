package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet108EnchantItem extends Packet
{
    public int windowId;

    /**
     * The position of the enchantment on the enchantment table window, starting with 0 as the topmost one.
     */
    public int enchantment;

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEnchantItem(this);
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.windowId = par1DataInputStream.readByte();
        this.enchantment = par1DataInputStream.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeByte(this.windowId);
        par1DataOutputStream.writeByte(this.enchantment);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 2;
    }
}
