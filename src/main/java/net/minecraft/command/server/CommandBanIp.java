package net.minecraft.command.server;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.BanEntry;
import net.minecraft.util.IChatComponent;

public class CommandBanIp extends CommandBase
{
    public static final Pattern field_147211_a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final String __OBFID = "CL_00000139";

    public String getCommandName()
    {
        return "ban-ip";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    // JAVADOC METHOD $$ func_71519_b
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isListActive() && super.canCommandSenderUseCommand(par1ICommandSender);
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.banip.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 1 && par2ArrayOfStr[0].length() > 1)
        {
            Matcher matcher = field_147211_a.matcher(par2ArrayOfStr[0]);
            IChatComponent ichatcomponent = null;

            if (par2ArrayOfStr.length >= 2)
            {
                ichatcomponent = func_147178_a(par1ICommandSender, par2ArrayOfStr, 1);
            }

            if (matcher.matches())
            {
                this.func_147210_a(par1ICommandSender, par2ArrayOfStr[0], ichatcomponent == null ? null : ichatcomponent.func_150260_c());
            }
            else
            {
                EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par2ArrayOfStr[0]);

                if (entityplayermp == null)
                {
                    throw new PlayerNotFoundException("commands.banip.invalid", new Object[0]);
                }

                this.func_147210_a(par1ICommandSender, entityplayermp.getPlayerIP(), ichatcomponent == null ? null : ichatcomponent.func_150260_c());
            }
        }
        else
        {
            throw new WrongUsageException("commands.banip.usage", new Object[0]);
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames()) : null;
    }

    protected void func_147210_a(ICommandSender p_147210_1_, String p_147210_2_, String p_147210_3_)
    {
        BanEntry banentry = new BanEntry(p_147210_2_);
        banentry.setBannedBy(p_147210_1_.getCommandSenderName());

        if (p_147210_3_ != null)
        {
            banentry.setBanReason(p_147210_3_);
        }

        MinecraftServer.getServer().getConfigurationManager().getBannedIPs().put(banentry);
        List list = MinecraftServer.getServer().getConfigurationManager().getPlayerList(p_147210_2_);
        String[] astring = new String[list.size()];
        int i = 0;
        EntityPlayerMP entityplayermp;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); astring[i++] = entityplayermp.getCommandSenderName())
        {
            entityplayermp = (EntityPlayerMP)iterator.next();
            entityplayermp.playerNetServerHandler.func_147360_c("You have been IP banned.");
        }

        if (list.isEmpty())
        {
            notifyAdmins(p_147210_1_, "commands.banip.success", new Object[] {p_147210_2_});
        }
        else
        {
            notifyAdmins(p_147210_1_, "commands.banip.success.players", new Object[] {p_147210_2_, joinNiceString(astring)});
        }
    }
}