package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature
{
    private float field_98056_d = -1.0F;
    private float field_98057_e;
    private static final String __OBFID = "CL_00001530";

    public EntityAgeable(World par1World)
    {
        super(par1World);
    }

    public abstract EntityAgeable createChild(EntityAgeable var1);

    // JAVADOC METHOD $$ func_70085_c
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        ItemStack itemstack = par1EntityPlayer.inventory.getCurrentItem();

        if (itemstack != null && itemstack.getItem() == Items.spawn_egg)
        {
            if (!this.worldObj.isRemote)
            {
                Class oclass = EntityList.getClassFromID(itemstack.getItemDamage());

                if (oclass != null && oclass.isAssignableFrom(this.getClass()))
                {
                    EntityAgeable entityageable = this.createChild(this);

                    if (entityageable != null)
                    {
                        entityageable.setGrowingAge(-24000);
                        entityageable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                        this.worldObj.spawnEntityInWorld(entityageable);

                        if (itemstack.hasDisplayName())
                        {
                            entityageable.setCustomNameTag(itemstack.getDisplayName());
                        }

                        if (!par1EntityPlayer.capabilities.isCreativeMode)
                        {
                            --itemstack.stackSize;

                            if (itemstack.stackSize <= 0)
                            {
                                par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                            }
                        }
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(12, new Integer(0));
    }

    // JAVADOC METHOD $$ func_70874_b
    public int getGrowingAge()
    {
        return this.dataWatcher.getWatchableObjectInt(12);
    }

    // JAVADOC METHOD $$ func_110195_a
    public void addGrowth(int par1)
    {
        int j = this.getGrowingAge();
        j += par1 * 20;

        if (j > 0)
        {
            j = 0;
        }

        this.setGrowingAge(j);
    }

    // JAVADOC METHOD $$ func_70873_a
    public void setGrowingAge(int par1)
    {
        this.dataWatcher.updateObject(12, Integer.valueOf(par1));
        this.setScaleForAge(this.isChild());
    }

    // JAVADOC METHOD $$ func_70014_b
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("Age", this.getGrowingAge());
    }

    // JAVADOC METHOD $$ func_70037_a
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.setGrowingAge(par1NBTTagCompound.getInteger("Age"));
    }

    // JAVADOC METHOD $$ func_70636_d
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (this.worldObj.isRemote)
        {
            this.setScaleForAge(this.isChild());
        }
        else
        {
            int i = this.getGrowingAge();

            if (i < 0)
            {
                ++i;
                this.setGrowingAge(i);
            }
            else if (i > 0)
            {
                --i;
                this.setGrowingAge(i);
            }
        }
    }

    // JAVADOC METHOD $$ func_70631_g_
    public boolean isChild()
    {
        return this.getGrowingAge() < 0;
    }

    // JAVADOC METHOD $$ func_98054_a
    public void setScaleForAge(boolean par1)
    {
        this.setScale(par1 ? 0.5F : 1.0F);
    }

    // JAVADOC METHOD $$ func_70105_a
    protected final void setSize(float par1, float par2)
    {
        boolean flag = this.field_98056_d > 0.0F;
        this.field_98056_d = par1;
        this.field_98057_e = par2;

        if (!flag)
        {
            this.setScale(1.0F);
        }
    }

    protected final void setScale(float par1)
    {
        super.setSize(this.field_98056_d * par1, this.field_98057_e * par1);
    }
}