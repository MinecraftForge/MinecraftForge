package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandXP extends CommandBase
{
    private static final String __OBFID = "CL_00000398";

    public String getCommandName()
    {
        return "xp";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.xp.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length <= 0)
        {
            throw new WrongUsageException("commands.xp.usage", new Object[0]);
        }
        else
        {
            String s = par2ArrayOfStr[0];
            boolean flag = s.endsWith("l") || s.endsWith("L");

            if (flag && s.length() > 1)
            {
                s = s.substring(0, s.length() - 1);
            }

            int i = parseInt(par1ICommandSender, s);
            boolean flag1 = i < 0;

            if (flag1)
            {
                i *= -1;
            }

            EntityPlayerMP entityplayermp;

            if (par2ArrayOfStr.length > 1)
            {
                entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[1]);
            }
            else
            {
                entityplayermp = getCommandSenderAsPlayer(par1ICommandSender);
            }

            if (flag)
            {
                if (flag1)
                {
                    entityplayermp.addExperienceLevel(-i);
                    notifyAdmins(par1ICommandSender, "commands.xp.success.negative.levels", new Object[] {Integer.valueOf(i), entityplayermp.getCommandSenderName()});
                }
                else
                {
                    entityplayermp.addExperienceLevel(i);
                    notifyAdmins(par1ICommandSender, "commands.xp.success.levels", new Object[] {Integer.valueOf(i), entityplayermp.getCommandSenderName()});
                }
            }
            else
            {
                if (flag1)
                {
                    throw new WrongUsageException("commands.xp.failure.widthdrawXp", new Object[0]);
                }

                entityplayermp.addExperience(i);
                notifyAdmins(par1ICommandSender, "commands.xp.success", new Object[] {Integer.valueOf(i), entityplayermp.getCommandSenderName()});
            }
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getAllUsernames()) : null;
    }

    protected String[] getAllUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 1;
    }
}