package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetDefaultSpawnpoint extends CommandBase
{
    private static final String __OBFID = "CL_00000973";

    public String getCommandName()
    {
        return "setworldspawn";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.setworldspawn.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length == 3)
        {
            if (par1ICommandSender.getEntityWorld() == null)
            {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }

            byte b0 = 0;
            int l = b0 + 1;
            int i = parseIntBounded(par1ICommandSender, par2ArrayOfStr[b0], -30000000, 30000000);
            int j = parseIntBounded(par1ICommandSender, par2ArrayOfStr[l++], 0, 256);
            int k = parseIntBounded(par1ICommandSender, par2ArrayOfStr[l++], -30000000, 30000000);
            par1ICommandSender.getEntityWorld().setSpawnLocation(i, j, k);
            notifyAdmins(par1ICommandSender, "commands.setworldspawn.success", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
        }
        else
        {
            if (par2ArrayOfStr.length != 0)
            {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }

            ChunkCoordinates chunkcoordinates = getCommandSenderAsPlayer(par1ICommandSender).getPlayerCoordinates();
            par1ICommandSender.getEntityWorld().setSpawnLocation(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ);
            notifyAdmins(par1ICommandSender, "commands.setworldspawn.success", new Object[] {Integer.valueOf(chunkcoordinates.posX), Integer.valueOf(chunkcoordinates.posY), Integer.valueOf(chunkcoordinates.posZ)});
        }
    }
}