package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet230ModLoader extends Packet
{
    private static final int MAX_DATA_LENGTH = 65535;
    public int modId;
    public int packetType;
    public int[] dataInt = new int[0];
    public float[] dataFloat = new float[0];
    public double[] dataDouble = new double[0];
    public String[] dataString = new String[0];

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInputStream var1) throws IOException
    {
        this.modId = var1.readInt();
        this.packetType = var1.readInt();
        int var2 = var1.readInt();

        if (var2 > 65535)
        {
            throw new IOException(String.format("Integer data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(var2), Integer.valueOf(65535)}));
        }
        else
        {
            this.dataInt = new int[var2];
            int var3;

            for (var3 = 0; var3 < var2; ++var3)
            {
                this.dataInt[var3] = var1.readInt();
            }

            var3 = var1.readInt();

            if (var3 > 65535)
            {
                throw new IOException(String.format("Float data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(var3), Integer.valueOf(65535)}));
            }
            else
            {
                this.dataFloat = new float[var3];
                int var4;

                for (var4 = 0; var4 < var3; ++var4)
                {
                    this.dataFloat[var4] = var1.readFloat();
                }

                var4 = var1.readInt();

                if (var4 > 65535)
                {
                    throw new IOException(String.format("Double data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(var4), Integer.valueOf(65535)}));
                }
                else
                {
                    this.dataDouble = new double[var4];
                    int var5;

                    for (var5 = 0; var5 < var4; ++var5)
                    {
                        this.dataDouble[var5] = var1.readDouble();
                    }

                    var5 = var1.readInt();

                    if (var5 > 65535)
                    {
                        throw new IOException(String.format("String data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(var5), Integer.valueOf(65535)}));
                    }
                    else
                    {
                        this.dataString = new String[var5];

                        for (int var6 = 0; var6 < var5; ++var6)
                        {
                            int var7 = var1.readInt();

                            if (var7 > 65535)
                            {
                                throw new IOException(String.format("String length of %d is higher than the max (%d).", new Object[] {Integer.valueOf(var7), Integer.valueOf(65535)}));
                            }

                            byte[] var8 = new byte[var7];

                            var1.readFully(var8);

                            this.dataString[var6] = new String(var8);
                        }
                    }
                }
            }
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutputStream var1) throws IOException
    {
        if (this.dataInt != null && this.dataInt.length > 65535)
        {
            throw new IOException(String.format("Integer data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(this.dataInt.length), Integer.valueOf(65535)}));
        }
        else if (this.dataFloat != null && this.dataFloat.length > 65535)
        {
            throw new IOException(String.format("Float data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(this.dataFloat.length), Integer.valueOf(65535)}));
        }
        else if (this.dataDouble != null && this.dataDouble.length > 65535)
        {
            throw new IOException(String.format("Double data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(this.dataDouble.length), Integer.valueOf(65535)}));
        }
        else if (this.dataString != null && this.dataString.length > 65535)
        {
            throw new IOException(String.format("String data size of %d is higher than the max (%d).", new Object[] {Integer.valueOf(this.dataString.length), Integer.valueOf(65535)}));
        }
        else
        {
            var1.writeInt(this.modId);
            var1.writeInt(this.packetType);
            int var2;

            if (this.dataInt == null)
            {
                var1.writeInt(0);
            }
            else
            {
                var1.writeInt(this.dataInt.length);

                for (var2 = 0; var2 < this.dataInt.length; ++var2)
                {
                    var1.writeInt(this.dataInt[var2]);
                }
            }

            if (this.dataFloat == null)
            {
                var1.writeInt(0);
            }
            else
            {
                var1.writeInt(this.dataFloat.length);

                for (var2 = 0; var2 < this.dataFloat.length; ++var2)
                {
                    var1.writeFloat(this.dataFloat[var2]);
                }
            }

            if (this.dataDouble == null)
            {
                var1.writeInt(0);
            }
            else
            {
                var1.writeInt(this.dataDouble.length);

                for (var2 = 0; var2 < this.dataDouble.length; ++var2)
                {
                    var1.writeDouble(this.dataDouble[var2]);
                }
            }

            if (this.dataString == null)
            {
                var1.writeInt(0);
            }
            else
            {
                var1.writeInt(this.dataString.length);

                for (var2 = 0; var2 < this.dataString.length; ++var2)
                {
                    if (this.dataString[var2].length() > 65535)
                    {
                        throw new IOException(String.format("String length of %d is higher than the max (%d).", new Object[] {Integer.valueOf(this.dataString[var2].length()), Integer.valueOf(65535)}));
                    }

                    var1.writeInt(this.dataString[var2].length());
                    var1.writeBytes(this.dataString[var2]);
                }
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler var1)
    {
        ModLoaderMp.handleAllPackets(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        byte var1 = 0;
        int var3 = var1 + 4;
        var3 += 4;
        var3 += 4;
        var3 += this.dataInt != null ? this.dataInt.length * 4 : 0;
        var3 += 4;
        var3 = this.dataFloat != null ? this.dataFloat.length * 4 : 0;
        var3 += 4;
        var3 = this.dataDouble != null ? this.dataDouble.length * 8 : 0;
        var3 += 4;

        if (this.dataString != null)
        {
            for (int var2 = 0; var2 < this.dataString.length; ++var2)
            {
                var3 += 4;
                var3 += this.dataString[var2].length();
            }
        }

        return var3;
    }
}
