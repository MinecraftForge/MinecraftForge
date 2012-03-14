package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Packet60Explosion extends Packet
{
    public double explosionX;
    public double explosionY;
    public double explosionZ;
    public float explosionSize;
    public Set destroyedBlockPositions;

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream par1DataInputStream) throws IOException
    {
        this.explosionX = par1DataInputStream.readDouble();
        this.explosionY = par1DataInputStream.readDouble();
        this.explosionZ = par1DataInputStream.readDouble();
        this.explosionSize = par1DataInputStream.readFloat();
        int var2 = par1DataInputStream.readInt();
        this.destroyedBlockPositions = new HashSet();
        int var3 = (int)this.explosionX;
        int var4 = (int)this.explosionY;
        int var5 = (int)this.explosionZ;

        for (int var6 = 0; var6 < var2; ++var6)
        {
            int var7 = par1DataInputStream.readByte() + var3;
            int var8 = par1DataInputStream.readByte() + var4;
            int var9 = par1DataInputStream.readByte() + var5;
            this.destroyedBlockPositions.add(new ChunkPosition(var7, var8, var9));
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException
    {
        par1DataOutputStream.writeDouble(this.explosionX);
        par1DataOutputStream.writeDouble(this.explosionY);
        par1DataOutputStream.writeDouble(this.explosionZ);
        par1DataOutputStream.writeFloat(this.explosionSize);
        par1DataOutputStream.writeInt(this.destroyedBlockPositions.size());
        int var2 = (int)this.explosionX;
        int var3 = (int)this.explosionY;
        int var4 = (int)this.explosionZ;
        Iterator var5 = this.destroyedBlockPositions.iterator();

        while (var5.hasNext())
        {
            ChunkPosition var6 = (ChunkPosition)var5.next();
            int var7 = var6.x - var2;
            int var8 = var6.y - var3;
            int var9 = var6.z - var4;
            par1DataOutputStream.writeByte(var7);
            par1DataOutputStream.writeByte(var8);
            par1DataOutputStream.writeByte(var9);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleExplosion(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 32 + this.destroyedBlockPositions.size() * 3;
    }
}
