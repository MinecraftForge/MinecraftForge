package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_150055_b;
    private static final String __OBFID = "CL_00000225";

    public BlockRailDetector()
    {
        super(true);
        this.func_149675_a(true);
    }

    public int func_149738_a(World p_149738_1_)
    {
        return 20;
    }

    public boolean func_149744_f()
    {
        return true;
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        if (!p_149670_1_.isRemote)
        {
            int l = p_149670_1_.getBlockMetadata(p_149670_2_, p_149670_3_, p_149670_4_);

            if ((l & 8) == 0)
            {
                this.func_150054_a(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, l);
            }
        }
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (!p_149674_1_.isRemote)
        {
            int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);

            if ((l & 8) != 0)
            {
                this.func_150054_a(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, l);
            }
        }
    }

    public int func_149709_b(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
    {
        return (p_149709_1_.getBlockMetadata(p_149709_2_, p_149709_3_, p_149709_4_) & 8) != 0 ? 15 : 0;
    }

    public int func_149748_c(IBlockAccess p_149748_1_, int p_149748_2_, int p_149748_3_, int p_149748_4_, int p_149748_5_)
    {
        return (p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_) & 8) == 0 ? 0 : (p_149748_5_ == 1 ? 15 : 0);
    }

    private void func_150054_a(World p_150054_1_, int p_150054_2_, int p_150054_3_, int p_150054_4_, int p_150054_5_)
    {
        boolean flag = (p_150054_5_ & 8) != 0;
        boolean flag1 = false;
        float f = 0.125F;
        List list = p_150054_1_.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)p_150054_2_ + f), (double)p_150054_3_, (double)((float)p_150054_4_ + f), (double)((float)(p_150054_2_ + 1) - f), (double)((float)(p_150054_3_ + 1) - f), (double)((float)(p_150054_4_ + 1) - f)));

        if (!list.isEmpty())
        {
            flag1 = true;
        }

        if (flag1 && !flag)
        {
            p_150054_1_.setBlockMetadataWithNotify(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_5_ | 8, 3);
            p_150054_1_.func_147459_d(p_150054_2_, p_150054_3_, p_150054_4_, this);
            p_150054_1_.func_147459_d(p_150054_2_, p_150054_3_ - 1, p_150054_4_, this);
            p_150054_1_.func_147458_c(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_2_, p_150054_3_, p_150054_4_);
        }

        if (!flag1 && flag)
        {
            p_150054_1_.setBlockMetadataWithNotify(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_5_ & 7, 3);
            p_150054_1_.func_147459_d(p_150054_2_, p_150054_3_, p_150054_4_, this);
            p_150054_1_.func_147459_d(p_150054_2_, p_150054_3_ - 1, p_150054_4_, this);
            p_150054_1_.func_147458_c(p_150054_2_, p_150054_3_, p_150054_4_, p_150054_2_, p_150054_3_, p_150054_4_);
        }

        if (flag1)
        {
            p_150054_1_.func_147464_a(p_150054_2_, p_150054_3_, p_150054_4_, this, this.func_149738_a(p_150054_1_));
        }

        p_150054_1_.func_147453_f(p_150054_2_, p_150054_3_, p_150054_4_, this);
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.func_149726_b(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
        this.func_150054_a(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_, p_149726_1_.getBlockMetadata(p_149726_2_, p_149726_3_, p_149726_4_));
    }

    public boolean func_149740_M()
    {
        return true;
    }

    public int func_149736_g(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        if ((p_149736_1_.getBlockMetadata(p_149736_2_, p_149736_3_, p_149736_4_) & 8) > 0)
        {
            float f = 0.125F;
            List list = p_149736_1_.getEntitiesWithinAABB(EntityMinecartCommandBlock.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)p_149736_2_ + f), (double)p_149736_3_, (double)((float)p_149736_4_ + f), (double)((float)(p_149736_2_ + 1) - f), (double)((float)(p_149736_3_ + 1) - f), (double)((float)(p_149736_4_ + 1) - f)));

            if (list.size() > 0)
            {
                return ((EntityMinecartCommandBlock)list.get(0)).func_145822_e().func_145760_g();
            }

            List list1 = p_149736_1_.selectEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getAABBPool().getAABB((double)((float)p_149736_2_ + f), (double)p_149736_3_, (double)((float)p_149736_4_ + f), (double)((float)(p_149736_2_ + 1) - f), (double)((float)(p_149736_3_ + 1) - f), (double)((float)(p_149736_4_ + 1) - f)), IEntitySelector.selectInventories);

            if (list1.size() > 0)
            {
                return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
            }
        }

        return 0;
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_150055_b = new IIcon[2];
        this.field_150055_b[0] = p_149651_1_.registerIcon(this.func_149641_N());
        this.field_150055_b[1] = p_149651_1_.registerIcon(this.func_149641_N() + "_powered");
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return (p_149691_2_ & 8) != 0 ? this.field_150055_b[1] : this.field_150055_b[0];
    }
}