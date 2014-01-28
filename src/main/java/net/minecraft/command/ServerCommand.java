package net.minecraft.command;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerCommand
{
    // JAVADOC FIELD $$ field_73702_a
    public final String command;
    public final ICommandSender sender;
    private static final String __OBFID = "CL_00001779";

    public ServerCommand(String par1Str, ICommandSender par2ICommandSender)
    {
        this.command = par1Str;
        this.sender = par2ICommandSender;
    }
}