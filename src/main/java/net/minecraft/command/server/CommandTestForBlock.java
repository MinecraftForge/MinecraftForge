package net.minecraft.command.server;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase
{
    private static final String __OBFID = "CL_00001181";

    public String getCommandName()
    {
        return "testforblock";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.testforblock.usage";
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
            Block block = Block.func_149684_b(par2ArrayOfStr[3]);

            if (block == null)
            {
                throw new NumberInvalidException("commands.setblock.notFound", new Object[] {par2ArrayOfStr[3]});
            }
            else
            {
                int l = -1;

                if (par2ArrayOfStr.length >= 5)
                {
                    l = parseIntBounded(par1ICommandSender, par2ArrayOfStr[4], -1, 15);
                }

                World world = par1ICommandSender.getEntityWorld();

                if (!world.blockExists(i, j, k))
                {
                    throw new CommandException("commands.testforblock.outOfWorld", new Object[0]);
                }
                else
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    boolean flag = false;

                    if (par2ArrayOfStr.length >= 6 && block.func_149716_u())
                    {
                        String s = func_147178_a(par1ICommandSender, par2ArrayOfStr, 5).func_150260_c();

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

                    Block block1 = world.func_147439_a(i, j, k);

                    if (block1 != block)
                    {
                        throw new CommandException("commands.testforblock.failed.tile", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), block1.func_149732_F(), block.func_149732_F()});
                    }
                    else
                    {
                        if (l > -1)
                        {
                            int i1 = world.getBlockMetadata(i, j, k);

                            if (i1 != l)
                            {
                                throw new CommandException("commands.testforblock.failed.data", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i1), Integer.valueOf(l)});
                            }
                        }

                        if (flag)
                        {
                            TileEntity tileentity = world.func_147438_o(i, j, k);

                            if (tileentity == null)
                            {
                                throw new CommandException("commands.testforblock.failed.tileEntity", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
                            }

                            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                            tileentity.func_145841_b(nbttagcompound1);

                            if (!this.func_147181_a(nbttagcompound, nbttagcompound1))
                            {
                                throw new CommandException("commands.testforblock.failed.nbt", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)});
                            }
                        }

                        par1ICommandSender.func_145747_a(new ChatComponentTranslation("commands.testforblock.success", new Object[] {Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k)}));
                    }
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.testforblock.usage", new Object[0]);
        }
    }

    public boolean func_147181_a(NBTBase p_147181_1_, NBTBase p_147181_2_)
    {
        if (p_147181_1_ == p_147181_2_)
        {
            return true;
        }
        else if (p_147181_1_ == null)
        {
            return true;
        }
        else if (p_147181_2_ == null)
        {
            return false;
        }
        else if (!p_147181_1_.getClass().equals(p_147181_2_.getClass()))
        {
            return false;
        }
        else if (p_147181_1_ instanceof NBTTagCompound)
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)p_147181_1_;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)p_147181_2_;
            Iterator iterator = nbttagcompound.func_150296_c().iterator();
            String s;
            NBTBase nbtbase2;

            do
            {
                if (!iterator.hasNext())
                {
                    return true;
                }

                s = (String)iterator.next();
                nbtbase2 = nbttagcompound.getTag(s);
            }
            while (this.func_147181_a(nbtbase2, nbttagcompound1.getTag(s)));

            return false;
        }
        else
        {
            return p_147181_1_.equals(p_147181_2_);
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 4 ? getListOfStringsFromIterableMatchingLastWord(par2ArrayOfStr, Block.field_149771_c.func_148742_b()) : null;
    }
}