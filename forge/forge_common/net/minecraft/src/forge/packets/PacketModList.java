package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PacketModList extends ForgePacket
{
    private boolean isServer = false;
    public String[] Mods;
    public int Length = -1;

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
            return ret.toString();
        }
        else
        {
            return toString();
        }
    }

}
