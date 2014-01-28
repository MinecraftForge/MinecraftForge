package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class CommandPlaySound extends CommandBase
{
    private static final String __OBFID = "CL_00000774";

    public String getCommandName()
    {
        return "playsound";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.playsound.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(par1ICommandSender), new Object[0]);
        }
        else
        {
            byte b0 = 0;
            int i = b0 + 1;
            String s = par2ArrayOfStr[b0];
            EntityPlayerMP entityplayermp = getPlayer(par1ICommandSender, par2ArrayOfStr[i++]);
            double d0 = (double)entityplayermp.getPlayerCoordinates().posX;
            double d1 = (double)entityplayermp.getPlayerCoordinates().posY;
            double d2 = (double)entityplayermp.getPlayerCoordinates().posZ;
            double d3 = 1.0D;
            double d4 = 1.0D;
            double d5 = 0.0D;

            if (par2ArrayOfStr.length > i)
            {
                d0 = func_110666_a(par1ICommandSender, d0, par2ArrayOfStr[i++]);
            }

            if (par2ArrayOfStr.length > i)
            {
                d1 = func_110665_a(par1ICommandSender, d1, par2ArrayOfStr[i++], 0, 0);
            }

            if (par2ArrayOfStr.length > i)
            {
                d2 = func_110666_a(par1ICommandSender, d2, par2ArrayOfStr[i++]);
            }

            if (par2ArrayOfStr.length > i)
            {
                d3 = func_110661_a(par1ICommandSender, par2ArrayOfStr[i++], 0.0D, 3.4028234663852886E38D);
            }

            if (par2ArrayOfStr.length > i)
            {
                d4 = func_110661_a(par1ICommandSender, par2ArrayOfStr[i++], 0.0D, 2.0D);
            }

            if (par2ArrayOfStr.length > i)
            {
                d5 = func_110661_a(par1ICommandSender, par2ArrayOfStr[i++], 0.0D, 1.0D);
            }

            double d6 = d3 > 1.0D ? d3 * 16.0D : 16.0D;
            double d7 = entityplayermp.getDistance(d0, d1, d2);

            if (d7 > d6)
            {
                if (d5 <= 0.0D)
                {
                    throw new CommandException("commands.playsound.playerTooFar", new Object[] {entityplayermp.getCommandSenderName()});
                }

                double d8 = d0 - entityplayermp.posX;
                double d9 = d1 - entityplayermp.posY;
                double d10 = d2 - entityplayermp.posZ;
                double d11 = Math.sqrt(d8 * d8 + d9 * d9 + d10 * d10);
                double d12 = entityplayermp.posX;
                double d13 = entityplayermp.posY;
                double d14 = entityplayermp.posZ;

                if (d11 > 0.0D)
                {
                    d12 += d8 / d11 * 2.0D;
                    d13 += d9 / d11 * 2.0D;
                    d14 += d10 / d11 * 2.0D;
                }

                entityplayermp.playerNetServerHandler.func_147359_a(new S29PacketSoundEffect(s, d12, d13, d14, (float)d5, (float)d4));
            }
            else
            {
                entityplayermp.playerNetServerHandler.func_147359_a(new S29PacketSoundEffect(s, d0, d1, d2, (float)d3, (float)d4));
            }

            notifyAdmins(par1ICommandSender, "commands.playsound.success", new Object[] {s, entityplayermp.getCommandSenderName()});
        }
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 1;
    }
}