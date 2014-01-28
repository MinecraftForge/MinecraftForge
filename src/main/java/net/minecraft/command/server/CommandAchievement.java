package net.minecraft.command.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class CommandAchievement extends CommandBase
{
    private static final String __OBFID = "CL_00000113";

    public String getCommandName()
    {
        return "achievement";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.achievement.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 2)
        {
            StatBase statbase = StatList.func_151177_a(par2ArrayOfStr[1]);

            if (statbase == null && !par2ArrayOfStr[1].equals("*"))
            {
                throw new CommandException("commands.achievement.unknownAchievement", new Object[] {par2ArrayOfStr[1]});
            }

            EntityPlayerMP entityplayermp;

            if (par2ArrayOfStr.length >= 3)
            {
                entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[2]);
            }
            else
            {
                entityplayermp = getCommandSenderAsPlayer(par1ICommandSender);
            }

            if (par2ArrayOfStr[0].equalsIgnoreCase("give"))
            {
                if (statbase == null)
                {
                    Iterator iterator = AchievementList.achievementList.iterator();

                    while (iterator.hasNext())
                    {
                        Achievement achievement = (Achievement)iterator.next();
                        entityplayermp.triggerAchievement(achievement);
                    }

                    notifyAdmins(par1ICommandSender, "commands.achievement.give.success.all", new Object[] {entityplayermp.getCommandSenderName()});
                }
                else
                {
                    if (statbase instanceof Achievement)
                    {
                        Achievement achievement2 = (Achievement)statbase;
                        ArrayList arraylist;

                        for (arraylist = Lists.newArrayList(); achievement2.parentAchievement != null && !entityplayermp.func_147099_x().hasAchievementUnlocked(achievement2.parentAchievement); achievement2 = achievement2.parentAchievement)
                        {
                            arraylist.add(achievement2.parentAchievement);
                        }

                        Iterator iterator1 = Lists.reverse(arraylist).iterator();

                        while (iterator1.hasNext())
                        {
                            Achievement achievement1 = (Achievement)iterator1.next();
                            entityplayermp.triggerAchievement(achievement1);
                        }
                    }

                    entityplayermp.triggerAchievement(statbase);
                    notifyAdmins(par1ICommandSender, "commands.achievement.give.success.one", new Object[] {entityplayermp.getCommandSenderName(), statbase.func_150955_j()});
                }

                return;
            }
        }

        throw new WrongUsageException("commands.achievement.usage", new Object[0]);
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 1)
        {
            // JAVADOC METHOD $$ func_71530_a
            return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"give"});
        }
        else if (par2ArrayOfStr.length != 2)
        {
            return par2ArrayOfStr.length == 3 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
        }
        else
        {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = StatList.allStats.iterator();

            while (iterator.hasNext())
            {
                StatBase statbase = (StatBase)iterator.next();
                arraylist.add(statbase.statId);
            }

            // JAVADOC METHOD $$ func_71531_a
            return getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, arraylist);
        }
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 2;
    }
}