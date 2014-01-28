package net.minecraft.command;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public interface ICommandSender
{
    // JAVADOC METHOD $$ func_70005_c_
    String getCommandSenderName();

    IChatComponent func_145748_c_();

    void func_145747_a(IChatComponent var1);

    // JAVADOC METHOD $$ func_70003_b
    boolean canCommandSenderUseCommand(int var1, String var2);

    // JAVADOC METHOD $$ func_82114_b
    ChunkCoordinates getPlayerCoordinates();

    World getEntityWorld();
}