package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

public class PacketModList extends ForgePacket
{
    private boolean isServer = false;
    public String[] Mods;
    public Hashtable<Integer, String> ModIDs = new Hashtable<Integer, String>();
    public int Length = -1;
    public boolean has4096 = false;

    public PacketModList(boolean server)
    {
        isServer = server;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException
    {
        if (!isServer)
        {
            data.writeInt(Mods.length);
            for (String mod : Mods)
            {
                data.writeUTF(mod);
            }
        }
        else
        {
            data.writeInt(ModIDs.size());
            for (Entry<Integer, String> entry : ModIDs.entrySet())
            {
                data.writeInt(entry.getKey());
                data.writeUTF(entry.getValue());
            }
        }
        data.writeBoolean(true);
    }

    @Override
    public void readData(DataInputStream data) throws IOException
    {
        if (isServer)
        {
            Length = data.readInt();
            if (Length >= 0)
            {
                Mods = new String[Length];
                for (int x = 0; x < Length; x++)
                {
                    Mods[x] = data.readUTF();
                }
            }
        }
        else
        {
            Length = data.readInt();
            for (int x = 0; x < Length; x++)
            {
                ModIDs.put(data.readInt(), data.readUTF());
            }
        }
        
        try 
        {
            has4096 = data.readBoolean();
        }
        catch (EOFException e)
        {
            has4096 = false;
        }
    }

    @Override
    public int getID()
    {
        return ForgePacket.MODLIST;
    }

    @Override
    public String toString(boolean full)
    {
        if (full)
        {
            StringBuilder ret = new StringBuilder();
            ret.append(toString()).append('\n');
            if (Mods != null)
            {
                for (String mod : Mods)
                {
                    ret.append("    " + mod + '\n');
                }
            }
            else if (ModIDs.size() != 0)
            {
                for (Entry<Integer, String> mod : ModIDs.entrySet())
                {
                    ret.append(String.format("    %03d ", mod.getKey()) + mod.getValue() + '\n');
                }
            }
            return ret.toString();
        }
        else
        {
            return toString();
        }
    }

}
