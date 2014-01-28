package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase
{
    private static final String __OBFID = "CL_00001158";

    public String getCommandName()
    {
        return "summon";
    }

    // JAVADOC METHOD $$ func_82362_a
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return "commands.summon.usage";
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 1)
        {
            String s = par2ArrayOfStr[0];
            double d0 = (double)par1ICommandSender.getPlayerCoordinates().posX + 0.5D;
            double d1 = (double)par1ICommandSender.getPlayerCoordinates().posY;
            double d2 = (double)par1ICommandSender.getPlayerCoordinates().posZ + 0.5D;

            if (par2ArrayOfStr.length >= 4)
            {
                d0 = func_110666_a(par1ICommandSender, d0, par2ArrayOfStr[1]);
                d1 = func_110666_a(par1ICommandSender, d1, par2ArrayOfStr[2]);
                d2 = func_110666_a(par1ICommandSender, d2, par2ArrayOfStr[3]);
            }

            World world = par1ICommandSender.getEntityWorld();

            if (!world.blockExists((int)d0, (int)d1, (int)d2))
            {
                notifyAdmins(par1ICommandSender, "commands.summon.outOfWorld", new Object[0]);
            }
            else
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (par2ArrayOfStr.length >= 5)
                {
                    IChatComponent ichatcomponent = func_147178_a(par1ICommandSender, par2ArrayOfStr, 4);

                    try
                    {
                        NBTBase nbtbase = JsonToNBT.func_150315_a(ichatcomponent.func_150260_c());

                        if (!(nbtbase instanceof NBTTagCompound))
                        {
                            notifyAdmins(par1ICommandSender, "commands.summon.tagError", new Object[] {"Not a valid tag"});
                            return;
                        }

                        nbttagcompound = (NBTTagCompound)nbtbase;
                        flag = true;
                    }
                    catch (NBTException nbtexception)
                    {
                        notifyAdmins(par1ICommandSender, "commands.summon.tagError", new Object[] {nbtexception.getMessage()});
                        return;
                    }
                }

                nbttagcompound.setString("id", s);
                Entity entity1 = EntityList.createEntityFromNBT(nbttagcompound, world);

                if (entity1 != null)
                {
                    entity1.setLocationAndAngles(d0, d1, d2, entity1.rotationYaw, entity1.rotationPitch);

                    if (!flag && entity1 instanceof EntityLiving)
                    {
                        ((EntityLiving)entity1).onSpawnWithEgg((IEntityLivingData)null);
                    }

                    world.spawnEntityInWorld(entity1);
                    Entity entity2 = entity1;

                    for (NBTTagCompound nbttagcompound1 = nbttagcompound; nbttagcompound1.func_150297_b("Riding", 10); nbttagcompound1 = nbttagcompound1.getCompoundTag("Riding"))
                    {
                        Entity entity = EntityList.createEntityFromNBT(nbttagcompound1.getCompoundTag("Riding"), world);

                        if (entity != null)
                        {
                            entity.setLocationAndAngles(d0, d1, d2, entity.rotationYaw, entity.rotationPitch);
                            world.spawnEntityInWorld(entity);
                            entity2.mountEntity(entity);
                        }

                        entity2 = entity;
                    }

                    notifyAdmins(par1ICommandSender, "commands.summon.success", new Object[0]);
                }
                else
                {
                    notifyAdmins(par1ICommandSender, "commands.summon.failed", new Object[0]);
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.summon.usage", new Object[0]);
        }
    }

    // JAVADOC METHOD $$ func_71516_a
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.func_147182_d()) : null;
    }

    protected String[] func_147182_d()
    {
        return (String[])EntityList.func_151515_b().toArray(new String[0]);
    }
}