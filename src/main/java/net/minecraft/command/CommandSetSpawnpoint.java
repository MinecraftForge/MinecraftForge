package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

public class CommandSetSpawnpoint extends CommandBase
{
    private static final String __OBFID = "CL_00001026";

    public String getCommandName()
    {
        return "spawnpoint";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.spawnpoint.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        EntityPlayerMP entityplayermp = par2ArrayOfStr.length == 0 ? getCommandSenderAsPlayer(par1ICommandSender) : getPlayer(par1ICommandSender, par2ArrayOfStr[0]);

        if (par2ArrayOfStr.length == 4)
        {
            if (entityplayermp.worldObj != null)
            {
                byte b0 = 1;
                int i = 30000000;
                int i1 = b0 + 1;
                int j = parseIntBounded(par1ICommandSender, par2ArrayOfStr[b0], -i, i);
                int k = parseIntBounded(par1ICommandSender, par2ArrayOfStr[i1++], 0, 256);
                int l = parseIntBounded(par1ICommandSender, par2ArrayOfStr[i1++], -i, i);
                entityplayermp.setSpawnChunk(new ChunkCoordinates(j, k, l), true);
                notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {entityplayermp.getCommandSenderName(), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(l)});
            }
        }
        else
        {
            if (par2ArrayOfStr.length > 1)
            {
                throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]);
            }

            ChunkCoordinates chunkcoordinates = entityplayermp.getPlayerCoordinates();
            entityplayermp.setSpawnChunk(chunkcoordinates, true);
            notifyAdmins(par1ICommandSender, "commands.spawnpoint.success", new Object[] {entityplayermp.getCommandSenderName(), Integer.valueOf(chunkcoordinates.posX), Integer.valueOf(chunkcoordinates.posY), Integer.valueOf(chunkcoordinates.posZ)});
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length != 1 && par2ArrayOfStr.length != 2 ? null : getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }

    // JAVADOC METHOD $$ func_82358_a
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}