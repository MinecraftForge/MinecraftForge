package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase
{
    private static final String __OBFID = "CL_00001183";

    public String getCommandName()
    {
        return "time";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.time.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length > 1)
        {
            int i;

            if (par2ArrayOfStr[0].equals("set"))
            {
                if (par2ArrayOfStr[1].equals("day"))
                {
                    i = 1000;
                }
                else if (par2ArrayOfStr[1].equals("night"))
                {
                    i = 13000;
                }
                else
                {
                    i = parseIntWithMin(par1ICommandSender, par2ArrayOfStr[1], 0);
                }

                this.setTime(par1ICommandSender, i);
                notifyAdmins(par1ICommandSender, "commands.time.set", new Object[] {Integer.valueOf(i)});
                return;
            }

            if (par2ArrayOfStr[0].equals("add"))
            {
                i = parseIntWithMin(par1ICommandSender, par2ArrayOfStr[1], 0);
                this.addTime(par1ICommandSender, i);
                notifyAdmins(par1ICommandSender, "commands.time.added", new Object[] {Integer.valueOf(i)});
                return;
            }
        }

        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"set", "add"}): (par2ArrayOfStr.length == 2 && par2ArrayOfStr[0].equals("set") ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"day", "night"}): null);
    }

    // JAVADOC METHOD $$ func_71552_a
    protected void setTime(ICommandSender par1ICommandSender, int par2)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            MinecraftServer.getServer().worldServers[j].setWorldTime((long)par2);
        }
    }

    // JAVADOC METHOD $$ func_71553_b
    protected void addTime(ICommandSender par1ICommandSender, int par2)
    {
        for (int j = 0; j < MinecraftServer.getServer().worldServers.length; ++j)
        {
            WorldServer worldserver = MinecraftServer.getServer().worldServers[j];
            worldserver.setWorldTime(worldserver.getWorldTime() + (long)par2);
        }
    }
}