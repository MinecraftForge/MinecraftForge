package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;

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
            for(String mod : Mods)
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

}
