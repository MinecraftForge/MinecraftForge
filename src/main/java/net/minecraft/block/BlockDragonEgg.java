package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block
{
    private static final String __OBFID = "CL_00000232";

    public BlockDragonEgg()
    {
        super(Material.field_151566_D);
        this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    public void func_149726_b(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        p_149726_1_.func_147464_a(p_149726_2_, p_149726_3_, p_149726_4_, this, this.func_149738_a(p_149726_1_));
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        p_149695_1_.func_147464_a(p_149695_2_, p_149695_3_, p_149695_4_, this, this.func_149738_a(p_149695_1_));
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        this.func_150018_e(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
    }

    private void func_150018_e(World p_150018_1_, int p_150018_2_, int p_150018_3_, int p_150018_4_)
    {
        if (BlockFalling.func_149831_e(p_150018_1_, p_150018_2_, p_150018_3_ - 1, p_150018_4_) && p_150018_3_ >= 0)
        {
            byte b0 = 32;

            if (!BlockFalling.field_149832_M && p_150018_1_.checkChunksExist(p_150018_2_ - b0, p_150018_3_ - b0, p_150018_4_ - b0, p_150018_2_ + b0, p_150018_3_ + b0, p_150018_4_ + b0))
            {
                EntityFallingBlock entityfallingblock = new EntityFallingBlock(p_150018_1_, (double)((float)p_150018_2_ + 0.5F), (double)((float)p_150018_3_ + 0.5F), (double)((float)p_150018_4_ + 0.5F), this);
                p_150018_1_.spawnEntityInWorld(entityfallingblock);
            }
            else
            {
                p_150018_1_.func_147468_f(p_150018_2_, p_150018_3_, p_150018_4_);

                while (BlockFalling.func_149831_e(p_150018_1_, p_150018_2_, p_150018_3_ - 1, p_150018_4_) && p_150018_3_ > 0)
                {
                    --p_150018_3_;
                }

                if (p_150018_3_ > 0)
                {
                    p_150018_1_.func_147465_d(p_150018_2_, p_150018_3_, p_150018_4_, this, 0, 2);
                }
            }
        }
    }

    public boolean func_149727_a(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        this.func_150019_m(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_);
        return true;
    }

    public void func_149699_a(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_)
    {
        this.func_150019_m(p_149699_1_, p_149699_2_, p_149699_3_, p_149699_4_);
    }

    private void func_150019_m(World p_150019_1_, int p_150019_2_, int p_150019_3_, int p_150019_4_)
    {
        if (p_150019_1_.func_147439_a(p_150019_2_, p_150019_3_, p_150019_4_) == this)
        {
            for (int l = 0; l < 1000; ++l)
            {
                int i1 = p_150019_2_ + p_150019_1_.rand.nextInt(16) - p_150019_1_.rand.nextInt(16);
                int j1 = p_150019_3_ + p_150019_1_.rand.nextInt(8) - p_150019_1_.rand.nextInt(8);
                int k1 = p_150019_4_ + p_150019_1_.rand.nextInt(16) - p_150019_1_.rand.nextInt(16);

                if (p_150019_1_.func_147439_a(i1, j1, k1).field_149764_J == Material.field_151579_a)
                {
                    if (!p_150019_1_.isRemote)
                    {
                        p_150019_1_.func_147465_d(i1, j1, k1, this, p_150019_1_.getBlockMetadata(p_150019_2_, p_150019_3_, p_150019_4_), 2);
                        p_150019_1_.func_147468_f(p_150019_2_, p_150019_3_, p_150019_4_);
                    }
                    else
                    {
                        short short1 = 128;

                        for (int l1 = 0; l1 < short1; ++l1)
                        {
                            double d0 = p_150019_1_.rand.nextDouble();
                            float f = (p_150019_1_.rand.nextFloat() - 0.5F) * 0.2F;
                            float f1 = (p_150019_1_.rand.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (p_150019_1_.rand.nextFloat() - 0.5F) * 0.2F;
                            double d1 = (double)i1 + (double)(p_150019_2_ - i1) * d0 + (p_150019_1_.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            double d2 = (double)j1 + (double)(p_150019_3_ - j1) * d0 + p_150019_1_.rand.nextDouble() * 1.0D - 0.5D;
                            double d3 = (double)k1 + (double)(p_150019_4_ - k1) * d0 + (p_150019_1_.rand.nextDouble() - 0.5D) * 1.0D + 0.5D;
                            p_150019_1_.spawnParticle("portal", d1, d2, d3, (double)f, (double)f1, (double)f2);
                        }
                    }

                    return;
                }
            }
        }
    }

    public int func_149738_a(World p_149738_1_)
    {
        return 5;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return true;
    }

    public int func_149645_b()
    {
        return 27;
    }

    @SideOnly(Side.CLIENT)
    public Item func_149694_d(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.func_150899_d(0);
    }
}