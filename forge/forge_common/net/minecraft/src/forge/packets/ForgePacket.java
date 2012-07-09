package net.minecraft.src.forge.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.forge.ForgeHooks;

public abstract class ForgePacket
{
    //Forge Packet ID Constants.
    public static final int FORGE_ID = 0x040E9B47; //"Forge".hashCode();
    public static final int SPAWN       = 1;
    public static final int MODLIST     = 2;
    public static final int MOD_MISSING = 3;
    public static final int OPEN_GUI    = 5;
    public static final int TRACK       = 6;

    public Packet getPacket()
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(bytes);
        try
        {
            data.writeByte(getID());
            writeData(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "Forge";
        pkt.data    = bytes.toByteArray();
        pkt.length  = pkt.data.length;
        return pkt;
    }

    public abstract void writeData(DataOutputStream data) throws IOException;
    public abstract void readData(DataInputStream data) throws IOException;
    public abstract int getID();
    public String toString(boolean full)
    {
        return toString();
    }

    @Override
    public String toString()
    {
        return getID() + " " + getClass().getSimpleName();
    }
}
