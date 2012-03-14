package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map.Entry;

public class PacketOpenGUI extends ForgePacket
{
    public int WindowID;
    public int ModID;
    public int GuiID;
    public int X;
    public int Y;
    public int Z;

    public PacketOpenGUI(){}
    public PacketOpenGUI(int window, int mod, int id, int x, int y, int z)
    {
        WindowID = window;
        ModID = mod;
        GuiID = id;
        X = x;
        Y = y;
        Z = z;
    }

    @Override
    public void writeData(DataOutputStream data) throws IOException
    {
        data.writeInt(WindowID);
        data.writeInt(ModID);
        data.writeInt(GuiID);
        data.writeInt(X);
        data.writeInt(Y);
        data.writeInt(Z);
    }

    @Override
    public void readData(DataInputStream data) throws IOException
    {
        WindowID = data.readInt();
        ModID = data.readInt();
        GuiID = data.readInt();
        X = data.readInt();
        Y = data.readInt();
        Z = data.readInt();
    }

    @Override
    public int getID()
    {
        return ForgePacket.OPEN_GUI;
    }

    @Override
    public String toString(boolean full)
    {
        if (full)
        {
            StringBuilder ret = new StringBuilder();
            ret.append(toString() + '\n');
            ret.append("    Window: " + WindowID + '\n');
            ret.append("    Mod:    " + ModID + '\n');
            ret.append("    Gui:    " + GuiID + '\n');
            ret.append("    X:      " + X + '\n');
            ret.append("    Y:      " + Y + '\n');
            ret.append("    Z:      " + Z + '\n');
            return ret.toString();
        }
        else
        {
            return toString();
        }
    }
}
