package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;

public class PacketModIDs extends ForgePacket 
{
    public Hashtable<Integer, String> Mods = new Hashtable<Integer, String>();
    public int Length;
    
    @Override
    public void writeData(DataOutputStream data) throws IOException 
    {
        data.writeInt(Mods.size());
        for (Entry<Integer, String> entry : Mods.entrySet())
        {
            data.writeInt(entry.getKey());
            data.writeUTF(entry.getValue());
        }
    }

    @Override
    public void readData(DataInputStream data) throws IOException 
    {
        Length = data.readInt();
        for(int x = 0; x < Length; x++)
        {
            Mods.put(data.readInt(), data.readUTF());
        }
    }

    @Override
    public int getID() 
    {
        return ForgePacket.MOD_IDS;
    }

}
