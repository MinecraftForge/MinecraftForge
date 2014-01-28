package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class BlockCactus extends Block implements IPlantable
{
    @SideOnly(Side.CLIENT)
    private IIcon field_150041_a;
    @SideOnly(Side.CLIENT)
    private IIcon field_150040_b;
    private static final String __OBFID = "CL_00000210";

    protected BlockCactus()
    {
        super(Material.field_151570_A);
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.tabDecorations);
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (p_149674_1_.func_147437_c(p_149674_2_, p_149674_3_ + 1, p_149674_4_))
        {
            int l;

            for (l = 1; p_149674_1_.func_147439_a(p_149674_2_, p_149674_3_ - l, p_149674_4_) == this; ++l)
            {
                ;
            }

            if (l < 3)
            {
                int i1 = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);

                if (i1 == 15)
                {
                    p_149674_1_.func_147449_b(p_149674_2_, p_149674_3_ + 1, p_149674_4_, this);
                    p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, 0, 4);
                    this.func_149695_a(p_149674_1_, p_149674_2_, p_149674_3_ + 1, p_149674_4_, this);
                }
                else
                {
                    p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, i1 + 1, 4);
                }
            }
        }
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)((float)p_149668_2_ + f), (double)p_149668_3_, (double)((float)p_149668_4_ + f), (double)((float)(p_149668_2_ + 1) - f), (double)((float)(p_149668_3_ + 1) - f), (double)((float)(p_149668_4_ + 1) - f));
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB func_149633_g(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)((float)p_149633_2_ + f), (double)p_149633_3_, (double)((float)p_149633_4_ + f), (double)((float)(p_149633_2_ + 1) - f), (double)(p_149633_3_ + 1), (double)((float)(p_149633_4_ + 1) - f));
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_1_ == 1 ? this.field_150041_a : (p_149691_1_ == 0 ? this.field_150040_b : this.field_149761_L);
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public int func_149645_b()
    {
        return 13;
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return !super.func_149742_c(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_) ? false : this.func_149718_j(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!this.func_149718_j(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_))
        {
            p_149695_1_.func_147480_a(p_149695_2_, p_149695_3_, p_149695_4_, true);
        }
    }

    public boolean func_149718_j(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        if (p_149718_1_.func_147439_a(p_149718_2_ - 1, p_149718_3_, p_149718_4_).func_149688_o().isSolid())
        {
            return false;
        }
        else if (p_149718_1_.func_147439_a(p_149718_2_ + 1, p_149718_3_, p_149718_4_).func_149688_o().isSolid())
        {
            return false;
        }
        else if (p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_, p_149718_4_ - 1).func_149688_o().isSolid())
        {
            return false;
        }
        else if (p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_, p_149718_4_ + 1).func_149688_o().isSolid())
        {
            return false;
        }
        else
        {
            Block block = p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_ - 1, p_149718_4_);
            return block.canSustainPlant(p_149718_1_, p_149718_2_, p_149718_3_ - 1, p_149718_4_, ForgeDirection.UP, this);
        }
    }

    public void func_149670_a(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
    {
        p_149670_5_.attackEntityFrom(DamageSource.cactus, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149761_L = p_149651_1_.registerIcon(this.func_149641_N() + "_side");
        this.field_150041_a = p_149651_1_.registerIcon(this.func_149641_N() + "_top");
        this.field_150040_b = p_149651_1_.registerIcon(this.func_149641_N() + "_bottom");
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
    {
        return EnumPlantType.Desert;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z)
    {
        return this;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z)
    {
        return -1;
    }
}