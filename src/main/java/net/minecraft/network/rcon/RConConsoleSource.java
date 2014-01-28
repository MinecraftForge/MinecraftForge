package net.minecraft.network.rcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

@SideOnly(Side.SERVER)
public class RConConsoleSource implements ICommandSender
{
    // JAVADOC FIELD $$ field_70010_a
    public static final RConConsoleSource consoleBuffer = new RConConsoleSource();
    // JAVADOC FIELD $$ field_70009_b
    private StringBuffer buffer = new StringBuffer();
    private static final String __OBFID = "CL_00001800";

    // JAVADOC METHOD $$ func_70007_b
    public void resetLog()
    {
        this.buffer.setLength(0);
    }

    public String getChatBuffer()
    {
        return this.buffer.toString();
    }

    // JAVADOC METHOD $$ func_70005_c_
    public String getCommandSenderName()
    {
        return "Rcon";
    }

    public IChatComponent func_145748_c_()
    {
        return new ChatComponentText(this.getCommandSenderName());
    }

    public void func_145747_a(IChatComponent ichatcomponent)
    {
        this.buffer.append(ichatcomponent.func_150260_c());
    }

    // JAVADOC METHOD $$ func_70003_b
    public boolean canCommandSenderUseCommand(int par1, String par2Str)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_82114_b
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0, 0, 0);
    }

    public World getEntityWorld()
    {
        return MinecraftServer.getServer().getEntityWorld();
    }
}