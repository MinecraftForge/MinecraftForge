package net.minecraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityBeacon extends TileEntity implements IInventory
{
    public static final Potion[][] field_146009_a = new Potion[][] {{Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}};
    @SideOnly(Side.CLIENT)
    private long field_146016_i;
    @SideOnly(Side.CLIENT)
    private float field_146014_j;
    private boolean field_146015_k;
    private int field_146012_l = -1;
    private int field_146013_m;
    private int field_146010_n;
    private ItemStack field_146011_o;
    private String field_146008_p;
    private static final String __OBFID = "CL_00000339";

    public void func_145845_h()
    {
        if (this.field_145850_b.getTotalWorldTime() % 80L == 0L)
        {
            this.func_146003_y();
            this.func_146000_x();
        }
    }

    private void func_146000_x()
    {
        if (this.field_146015_k && this.field_146012_l > 0 && !this.field_145850_b.isRemote && this.field_146013_m > 0)
        {
            double d0 = (double)(this.field_146012_l * 10 + 10);
            byte b0 = 0;

            if (this.field_146012_l >= 4 && this.field_146013_m == this.field_146010_n)
            {
                b0 = 1;
            }

            AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB((double)this.field_145851_c, (double)this.field_145848_d, (double)this.field_145849_e, (double)(this.field_145851_c + 1), (double)(this.field_145848_d + 1), (double)(this.field_145849_e + 1)).expand(d0, d0, d0);
            axisalignedbb.maxY = (double)this.field_145850_b.getHeight();
            List list = this.field_145850_b.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
            Iterator iterator = list.iterator();
            EntityPlayer entityplayer;

            while (iterator.hasNext())
            {
                entityplayer = (EntityPlayer)iterator.next();
                entityplayer.addPotionEffect(new PotionEffect(this.field_146013_m, 180, b0, true));
            }

            if (this.field_146012_l >= 4 && this.field_146013_m != this.field_146010_n && this.field_146010_n > 0)
            {
                iterator = list.iterator();

                while (iterator.hasNext())
                {
                    entityplayer = (EntityPlayer)iterator.next();
                    entityplayer.addPotionEffect(new PotionEffect(this.field_146010_n, 180, 0, true));
                }
            }
        }
    }

    private void func_146003_y()
    {
        int i = this.field_146012_l;

        if (!this.field_145850_b.canBlockSeeTheSky(this.field_145851_c, this.field_145848_d + 1, this.field_145849_e))
        {
            this.field_146015_k = false;
            this.field_146012_l = 0;
        }
        else
        {
            this.field_146015_k = true;
            this.field_146012_l = 0;

            for (int j = 1; j <= 4; this.field_146012_l = j++)
            {
                int k = this.field_145848_d - j;

                if (k < 0)
                {
                    break;
                }

                boolean flag = true;

                for (int l = this.field_145851_c - j; l <= this.field_145851_c + j && flag; ++l)
                {
                    for (int i1 = this.field_145849_e - j; i1 <= this.field_145849_e + j; ++i1)
                    {
                        Block block = this.field_145850_b.func_147439_a(l, k, i1);

                        if (!block.isBeaconBase(this.field_145850_b, l, k, i1, field_145851_c, field_145848_d, field_145849_e))
                        {
                            flag = false;
                            break;
                        }
                    }
                }

                if (!flag)
                {
                    break;
                }
            }

            if (this.field_146012_l == 0)
            {
                this.field_146015_k = false;
            }
        }

        if (!this.field_145850_b.isRemote && this.field_146012_l == 4 && i < this.field_146012_l)
        {
            Iterator iterator = this.field_145850_b.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB((double)this.field_145851_c, (double)this.field_145848_d, (double)this.field_145849_e, (double)this.field_145851_c, (double)(this.field_145848_d - 4), (double)this.field_145849_e).expand(10.0D, 5.0D, 10.0D)).iterator();

            while (iterator.hasNext())
            {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();
                entityplayer.triggerAchievement(AchievementList.field_150965_K);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float func_146002_i()
    {
        if (!this.field_146015_k)
        {
            return 0.0F;
        }
        else
        {
            int i = (int)(this.field_145850_b.getTotalWorldTime() - this.field_146016_i);
            this.field_146016_i = this.field_145850_b.getTotalWorldTime();

            if (i > 1)
            {
                this.field_146014_j -= (float)i / 40.0F;

                if (this.field_146014_j < 0.0F)
                {
                    this.field_146014_j = 0.0F;
                }
            }

            this.field_146014_j += 0.025F;

            if (this.field_146014_j > 1.0F)
            {
                this.field_146014_j = 1.0F;
            }

            return this.field_146014_j;
        }
    }

    public int func_146007_j()
    {
        return this.field_146013_m;
    }

    public int func_146006_k()
    {
        return this.field_146010_n;
    }

    public int func_145998_l()
    {
        return this.field_146012_l;
    }

    @SideOnly(Side.CLIENT)
    public void func_146005_c(int p_146005_1_)
    {
        this.field_146012_l = p_146005_1_;
    }

    public void func_146001_d(int p_146001_1_)
    {
        this.field_146013_m = 0;

        for (int j = 0; j < this.field_146012_l && j < 3; ++j)
        {
            Potion[] apotion = field_146009_a[j];
            int k = apotion.length;

            for (int l = 0; l < k; ++l)
            {
                Potion potion = apotion[l];

                if (potion.id == p_146001_1_)
                {
                    this.field_146013_m = p_146001_1_;
                    return;
                }
            }
        }
    }

    public void func_146004_e(int p_146004_1_)
    {
        this.field_146010_n = 0;

        if (this.field_146012_l >= 4)
        {
            for (int j = 0; j < 4; ++j)
            {
                Potion[] apotion = field_146009_a[j];
                int k = apotion.length;

                for (int l = 0; l < k; ++l)
                {
                    Potion potion = apotion[l];

                    if (potion.id == p_146004_1_)
                    {
                        this.field_146010_n = p_146004_1_;
                        return;
                    }
                }
            }
        }
    }

    public Packet func_145844_m()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.func_145841_b(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 3, nbttagcompound);
    }

    @SideOnly(Side.CLIENT)
    public double func_145833_n()
    {
        return 65536.0D;
    }

    public void func_145839_a(NBTTagCompound p_145839_1_)
    {
        super.func_145839_a(p_145839_1_);
        this.field_146013_m = p_145839_1_.getInteger("Primary");
        this.field_146010_n = p_145839_1_.getInteger("Secondary");
        this.field_146012_l = p_145839_1_.getInteger("Levels");
    }

    public void func_145841_b(NBTTagCompound p_145841_1_)
    {
        super.func_145841_b(p_145841_1_);
        p_145841_1_.setInteger("Primary", this.field_146013_m);
        p_145841_1_.setInteger("Secondary", this.field_146010_n);
        p_145841_1_.setInteger("Levels", this.field_146012_l);
    }

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return 1;
    }

    // JAVADOC METHOD $$ func_70301_a
    public ItemStack getStackInSlot(int par1)
    {
        return par1 == 0 ? this.field_146011_o : null;
    }

    // JAVADOC METHOD $$ func_70298_a
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (par1 == 0 && this.field_146011_o != null)
        {
            if (par2 >= this.field_146011_o.stackSize)
            {
                ItemStack itemstack = this.field_146011_o;
                this.field_146011_o = null;
                return itemstack;
            }
            else
            {
                this.field_146011_o.stackSize -= par2;
                return new ItemStack(this.field_146011_o.getItem(), par2, this.field_146011_o.getItemDamage());
            }
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70304_b
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (par1 == 0 && this.field_146011_o != null)
        {
            ItemStack itemstack = this.field_146011_o;
            this.field_146011_o = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70299_a
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        if (par1 == 0)
        {
            this.field_146011_o = par2ItemStack;
        }
    }

    public String func_145825_b()
    {
        return this.func_145818_k_() ? this.field_146008_p : "container.beacon";
    }

    public boolean func_145818_k_()
    {
        return this.field_146008_p != null && this.field_146008_p.length() > 0;
    }

    public void func_145999_a(String p_145999_1_)
    {
        this.field_146008_p = p_145999_1_;
    }

    // JAVADOC METHOD $$ func_70297_j_
    public int getInventoryStackLimit()
    {
        return 1;
    }

    // JAVADOC METHOD $$ func_70300_a
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.field_145850_b.func_147438_o(this.field_145851_c, this.field_145848_d, this.field_145849_e) != this ? false : par1EntityPlayer.getDistanceSq((double)this.field_145851_c + 0.5D, (double)this.field_145848_d + 0.5D, (double)this.field_145849_e + 0.5D) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}

    // JAVADOC METHOD $$ func_94041_b
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return par2ItemStack.getItem() == Items.emerald || par2ItemStack.getItem() == Items.diamond || par2ItemStack.getItem() == Items.gold_ingot || par2ItemStack.getItem() == Items.iron_ingot;
    }
}