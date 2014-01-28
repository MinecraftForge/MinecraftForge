package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper
{
    // JAVADOC FIELD $$ field_96113_a
    private boolean isBlocked = true;
    private int transferTicker = -1;
    private static final String __OBFID = "CL_00001676";

    public EntityMinecartHopper(World par1World)
    {
        super(par1World);
    }

    public EntityMinecartHopper(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
    }

    public int getMinecartType()
    {
        return 5;
    }

    public Block func_145817_o()
    {
        return Blocks.hopper;
    }

    public int getDefaultDisplayTileOffset()
    {
        return 1;
    }

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return 5;
    }

    // JAVADOC METHOD $$ func_130002_c
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, par1EntityPlayer))) return true;
        if (!this.worldObj.isRemote)
        {
            par1EntityPlayer.displayGUIHopperMinecart(this);
        }

        return true;
    }

    // JAVADOC METHOD $$ func_96095_a
    public void onActivatorRailPass(int par1, int par2, int par3, boolean par4)
    {
        boolean flag1 = !par4;

        if (flag1 != this.getBlocked())
        {
            this.setBlocked(flag1);
        }
    }

    // JAVADOC METHOD $$ func_96111_ay
    public boolean getBlocked()
    {
        return this.isBlocked;
    }

    // JAVADOC METHOD $$ func_96110_f
    public void setBlocked(boolean par1)
    {
        this.isBlocked = par1;
    }

    public World func_145831_w()
    {
        return this.worldObj;
    }

    // JAVADOC METHOD $$ func_96107_aA
    public double getXPos()
    {
        return this.posX;
    }

    // JAVADOC METHOD $$ func_96109_aB
    public double getYPos()
    {
        return this.posY;
    }

    // JAVADOC METHOD $$ func_96108_aC
    public double getZPos()
    {
        return this.posZ;
    }

    // JAVADOC METHOD $$ func_70071_h_
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked())
        {
            --this.transferTicker;

            if (!this.canTransfer())
            {
                this.setTransferTicker(0);

                if (this.func_96112_aD())
                {
                    this.setTransferTicker(4);
                    this.onInventoryChanged();
                }
            }
        }
    }

    public boolean func_96112_aD()
    {
        if (TileEntityHopper.func_145891_a(this))
        {
            return true;
        }
        else
        {
            List list = this.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(0.25D, 0.0D, 0.25D), IEntitySelector.selectAnything);

            if (list.size() > 0)
            {
                TileEntityHopper.func_145898_a(this, (EntityItem)list.get(0));
            }

            return false;
        }
    }

    public void killMinecart(DamageSource par1DamageSource)
    {
        super.killMinecart(par1DamageSource);
        this.func_145778_a(Item.func_150898_a(Blocks.hopper), 1, 0.0F);
    }

    // JAVADOC METHOD $$ func_70014_b
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("TransferCooldown", this.transferTicker);
    }

    // JAVADOC METHOD $$ func_70037_a
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        this.transferTicker = par1NBTTagCompound.getInteger("TransferCooldown");
    }

    // JAVADOC METHOD $$ func_98042_n
    public void setTransferTicker(int par1)
    {
        this.transferTicker = par1;
    }

    // JAVADOC METHOD $$ func_98043_aE
    public boolean canTransfer()
    {
        return this.transferTicker > 0;
    }
}