package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase
{
    private static final String __OBFID = "CL_00000826";

    public String getCommandName()
    {
        return "save-all";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.save.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        par1ICommandSender.func_145747_a(new ChatComponentTranslation("commands.save.start", new Object[0]));

        if (minecraftserver.getConfigurationManager() != null)
        {
            minecraftserver.getConfigurationManager().saveAllPlayerData();
        }

        try
        {
            int i;
            WorldServer worldserver;
            boolean flag;

            for (i = 0; i < minecraftserver.worldServers.length; ++i)
            {
                if (minecraftserver.worldServers[i] != null)
                {
                    worldserver = minecraftserver.worldServers[i];
                    flag = worldserver.canNotSave;
                    worldserver.canNotSave = false;
                    worldserver.saveAllChunks(true, (IProgressUpdate)null);
                    worldserver.canNotSave = flag;
                }
            }

            if (par2ArrayOfStr.length > 0 && "flush".equals(par2ArrayOfStr[0]))
            {
                par1ICommandSender.func_145747_a(new ChatComponentTranslation("commands.save.flushStart", new Object[0]));

                for (i = 0; i < minecraftserver.worldServers.length; ++i)
                {
                    if (minecraftserver.worldServers[i] != null)
                    {
                        worldserver = minecraftserver.worldServers[i];
                        flag = worldserver.canNotSave;
                        worldserver.canNotSave = false;
                        worldserver.saveChunkData();
                        worldserver.canNotSave = flag;
                    }
                }

                par1ICommandSender.func_145747_a(new ChatComponentTranslation("commands.save.flushEnd", new Object[0]));
            }
        }
        catch (MinecraftException minecraftexception)
        {
            notifyAdmins(par1ICommandSender, "commands.save.failed", new Object[] {minecraftexception.getMessage()});
            return;
        }

        notifyAdmins(par1ICommandSender, "commands.save.success", new Object[0]);
    }
}