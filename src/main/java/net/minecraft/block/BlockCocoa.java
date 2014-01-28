package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCocoa extends BlockDirectional implements IGrowable
{
    @SideOnly(Side.CLIENT)
    private IIcon[] field_149989_a;
    private static final String __OBFID = "CL_00000216";

    public BlockCocoa()
    {
        super(Material.field_151585_k);
        this.func_149675_a(true);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        return this.field_149989_a[2];
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (!this.func_149718_j(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_))
        {
            this.func_149697_b(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_), 0);
            p_149674_1_.func_147465_d(p_149674_2_, p_149674_3_, p_149674_4_, func_149729_e(0), 0, 2);
        }
        else if (p_149674_1_.rand.nextInt(5) == 0)
        {
            int l = p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_);
            int i1 = func_149987_c(l);

            if (i1 < 2)
            {
                ++i1;
                p_149674_1_.setBlockMetadataWithNotify(p_149674_2_, p_149674_3_, p_149674_4_, i1 << 2 | func_149895_l(l), 2);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149988_b(int p_149988_1_)
    {
        if (p_149988_1_ < 0 || p_149988_1_ >= this.field_149989_a.length)
        {
            p_149988_1_ = this.field_149989_a.length - 1;
        }

        return this.field_149989_a[p_149988_1_];
    }

    public boolean func_149718_j(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        int l = func_149895_l(p_149718_1_.getBlockMetadata(p_149718_2_, p_149718_3_, p_149718_4_));
        p_149718_2_ += Direction.offsetX[l];
        p_149718_4_ += Direction.offsetZ[l];
        Block block = p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_, p_149718_4_);
        return block == Blocks.log && BlockLog.func_150165_c(p_149718_1_.getBlockMetadata(p_149718_2_, p_149718_3_, p_149718_4_)) == 3;
    }

    public int func_149645_b()
    {
        return 28;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        this.func_149719_a(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
        return super.func_149668_a(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
    }

    public void func_149719_a(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
    {
        int l = p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_);
        int i1 = func_149895_l(l);
        int j1 = func_149987_c(l);
        int k1 = 4 + j1 * 2;
        int l1 = 5 + j1 * 2;
        float f = (float)k1 / 2.0F;

        switch (i1)
        {
            case 0:
                this.func_149676_a((8.0F - f) / 16.0F, (12.0F - (float)l1) / 16.0F, (15.0F - (float)k1) / 16.0F, (8.0F + f) / 16.0F, 0.75F, 0.9375F);
                break;
            case 1:
                this.func_149676_a(0.0625F, (12.0F - (float)l1) / 16.0F, (8.0F - f) / 16.0F, (1.0F + (float)k1) / 16.0F, 0.75F, (8.0F + f) / 16.0F);
                break;
            case 2:
                this.func_149676_a((8.0F - f) / 16.0F, (12.0F - (float)l1) / 16.0F, 0.0625F, (8.0F + f) / 16.0F, 0.75F, (1.0F + (float)k1) / 16.0F);
                break;
            case 3:
                this.func_149676_a((15.0F - (float)k1) / 16.0F, (12.0F - (float)l1) / 16.0F, (8.0F - f) / 16.0F, 0.9375F, 0.75F, (8.0F + f) / 16.0F);
        }
    }

    public void func_149689_a(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        int l = ((MathHelper.floor_double((double)(p_149689_5_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 0) % 4;
        p_149689_1_.setBlockMetadataWithNotify(p_149689_2_, p_149689_3_, p_149689_4_, l, 2);
    }

    public int func_149660_a(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        if (p_149660_5_ == 1 || p_149660_5_ == 0)
        {
            p_149660_5_ = 2;
        }

        return Direction.rotateOpposite[Direction.facingToDirection[p_149660_5_]];
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        if (!this.func_149718_j(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_))
        {
            this.func_149697_b(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_), 0);
            p_149695_1_.func_147465_d(p_149695_2_, p_149695_3_, p_149695_4_, func_149729_e(0), 0, 2);
        }
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB func_149633_g(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
    {
        this.func_149719_a(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
        return super.func_149633_g(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
    }

    public static int func_149987_c(int p_149987_0_)
    {
        return (p_149987_0_ & 12) >> 2;
    }

    public void func_149690_a(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
    {
        super.func_149690_a(p_149690_1_, p_149690_2_, p_149690_3_, p_149690_4_, p_149690_5_, p_149690_6_, p_149690_7_);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int p_149690_5_, int fortune)
    {
        ArrayList<ItemStack> dropped = super.getDrops(world, x, y, z, p_149690_5_, fortune);
        int j1 = func_149987_c(p_149690_5_);
        byte b0 = 1;

        if (j1 >= 2)
        {
            b0 = 3;
        }

        for (int k1 = 0; k1 < b0; ++k1)
        {
            dropped.add(new ItemStack(Items.dye, 1, 3));
        }
        return dropped;
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Items.dye;
    }

    public int func_149643_k(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        return 3;
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_)
    {
        this.field_149989_a = new IIcon[3];

        for (int i = 0; i < this.field_149989_a.length; ++i)
        {
            this.field_149989_a[i] = p_149651_1_.registerIcon(this.func_149641_N() + "_stage_" + i);
        }
    }

    public boolean func_149851_a(World p_149851_1_, int p_149851_2_, int p_149851_3_, int p_149851_4_, boolean p_149851_5_)
    {
        int l = p_149851_1_.getBlockMetadata(p_149851_2_, p_149851_3_, p_149851_4_);
        int i1 = func_149987_c(l);
        return i1 < 2;
    }

    public boolean func_149852_a(World p_149852_1_, Random p_149852_2_, int p_149852_3_, int p_149852_4_, int p_149852_5_)
    {
        return true;
    }

    public void func_149853_b(World p_149853_1_, Random p_149853_2_, int p_149853_3_, int p_149853_4_, int p_149853_5_)
    {
        int l = p_149853_1_.getBlockMetadata(p_149853_3_, p_149853_4_, p_149853_5_);
        int i1 = BlockDirectional.func_149895_l(l);
        int j1 = func_149987_c(l);
        ++j1;
        p_149853_1_.setBlockMetadataWithNotify(p_149853_3_, p_149853_4_, p_149853_5_, j1 << 2 | i1, 2);
    }

    @Override
    public Item func_149650_a(int par1, Random par2Random, int par3)
    {
        return null;
    }
}