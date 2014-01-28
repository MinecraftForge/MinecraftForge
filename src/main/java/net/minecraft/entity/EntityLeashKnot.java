package net.minecraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging
{
    private static final String __OBFID = "CL_00001548";

    public EntityLeashKnot(World par1World)
    {
        super(par1World);
    }

    public EntityLeashKnot(World par1World, int par2, int par3, int par4)
    {
        super(par1World, par2, par3, par4, 0);
        this.setPosition((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D);
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    public void setDirection(int par1) {}

    public int getWidthPixels()
    {
        return 9;
    }

    public int getHeightPixels()
    {
        return 9;
    }

    // JAVADOC METHOD $$ func_70112_a
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double par1)
    {
        return par1 < 1024.0D;
    }

    // JAVADOC METHOD $$ func_110128_b
    public void onBroken(Entity par1Entity) {}

    // JAVADOC METHOD $$ func_70039_c
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70014_b
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    // JAVADOC METHOD $$ func_70037_a
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    // JAVADOC METHOD $$ func_130002_c
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.getHeldItem();
        boolean flag = false;
        double d0;
        List list;
        Iterator iterator;
        EntityLiving entityliving;

        if (itemstack != null && itemstack.getItem() == Items.lead && !this.worldObj.isRemote)
        {
            d0 = 7.0D;
            list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

            if (list != null)
            {
                iterator = list.iterator();

                while (iterator.hasNext())
                {
                    entityliving = (EntityLiving)iterator.next();

                    if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == par1EntityPlayer)
                    {
                        entityliving.setLeashedToEntity(this, true);
                        flag = true;
                    }
                }
            }
        }

        if (!this.worldObj.isRemote && !flag)
        {
            this.setDead();

            if (par1EntityPlayer.capabilities.isCreativeMode)
            {
                d0 = 7.0D;
                list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + d0, this.posZ + d0));

                if (list != null)
                {
                    iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        entityliving = (EntityLiving)iterator.next();

                        if (entityliving.getLeashed() && entityliving.getLeashedToEntity() == this)
                        {
                            entityliving.clearLeashed(true, false);
                        }
                    }
                }
            }
        }

        return true;
    }

    // JAVADOC METHOD $$ func_70518_d
    public boolean onValidSurface()
    {
        return this.worldObj.func_147439_a(this.field_146063_b, this.field_146064_c, this.field_146062_d).func_149645_b() == 11;
    }

    public static EntityLeashKnot func_110129_a(World par0World, int par1, int par2, int par3)
    {
        EntityLeashKnot entityleashknot = new EntityLeashKnot(par0World, par1, par2, par3);
        entityleashknot.forceSpawn = true;
        par0World.spawnEntityInWorld(entityleashknot);
        return entityleashknot;
    }

    public static EntityLeashKnot getKnotForBlock(World par0World, int par1, int par2, int par3)
    {
        List list = par0World.getEntitiesWithinAABB(EntityLeashKnot.class, AxisAlignedBB.getAABBPool().getAABB((double)par1 - 1.0D, (double)par2 - 1.0D, (double)par3 - 1.0D, (double)par1 + 1.0D, (double)par2 + 1.0D, (double)par3 + 1.0D));

        if (list != null)
        {
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityLeashKnot entityleashknot = (EntityLeashKnot)iterator.next();

                if (entityleashknot.field_146063_b == par1 && entityleashknot.field_146064_c == par2 && entityleashknot.field_146062_d == par3)
                {
                    return entityleashknot;
                }
            }
        }

        return null;
    }
}