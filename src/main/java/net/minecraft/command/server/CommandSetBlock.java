package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase
{
    private static final String __OBFID = "CL_00000949";

    public String getCommandName()
    {
        return "setblock";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.setblock.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 4)
        {
            int i = par1ICommandSender.getPlayerCoordinates().posX;
            int j = par1ICommandSender.getPlayerCoordinates().posY;
            int k = par1ICommandSender.getPlayerCoordinates().posZ;
            i = MathHelper.floor_double(func_110666_a(par1ICommandSender, (double)i, par2ArrayOfStr[0]));
            j = MathHelper.floor_double(func_110666_a(par1ICommandSender, (double)j, par2ArrayOfStr[1]));
            k = MathHelper.floor_double(func_110666_a(par1ICommandSender, (double)k, par2ArrayOfStr[2]));
            Block block = CommandBase.func_147180_g(par1ICommandSender, par2ArrayOfStr[3]);
            int l = 0;

            if (par2ArrayOfStr.length >= 5)
            {
                l = parseIntBounded(par1ICommandSender, par2ArrayOfStr[4], 0, 15);
            }

            World world = par1ICommandSender.getEntityWorld();

            if (!world.blockExists(i, j, k))
            {
                throw new CommandException("commands.setblock.outOfWorld", new Object[0]);
            }
            else
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (par2ArrayOfStr.length >= 7 && block.func_149716_u())
                {
                    String s = func_147178_a(par1ICommandSender, par2ArrayOfStr, 6).func_150260_c();

                    try
                    {
                        NBTBase nbtbase = JsonToNBT.func_150315_a(s);

                        if (!(nbtbase instanceof NBTTagCompound))
                        {
                            throw new CommandException("commands.setblock.tagError", new Object[] {"Not a valid tag"});
                        }

                        nbttagcompound = (NBTTagCompound)nbtbase;
                        flag = true;
                    }
                    catch (NBTException nbtexception)
                    {
                        throw new CommandException("commands.setblock.tagError", new Object[] {nbtexception.getMessage()});
                    }
                }

                if (par2ArrayOfStr.length >= 6)
                {
                    if (par2ArrayOfStr[5].equals("destroy"))
                    {
                        world.func_147480_a(i, j, k, true);
                    }
                    else if (par2ArrayOfStr[5].equals("keep") && !world.func_147437_c(i, j, k))
                    {
                        throw new CommandException("commands.setblock.noChange", new Object[0]);
                    }
                }

                if (!world.func_147465_d(i, j, k, block, l, 3))
                {
                    throw new CommandException("commands.setblock.noChange", new Object[0]);
                }
                else
                {
                    if (flag)
                    {
                        TileEntity tileentity = world.func_147438_o(i, j, k);

                        if (tileentity != null)
                        {
                            nbttagcompound.setInteger("x", i);
                            nbttagcompound.setInteger("y", j);
                            nbttagcompound.setInteger("z", k);
                            tileentity.func_145839_a(nbttagcompound);
                        }
                    }

                    notifyAdmins(par1ICommandSender, "commands.setblock.success", new Object[0]);
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.setblock.usage", new Object[0]);
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 4 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Block.field_149771_c.func_148742_b()) : (par2ArrayOfStr.length == 6 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"replace", "destroy", "keep"}): null);
    }
}