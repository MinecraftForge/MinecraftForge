package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class BlockSilverfish extends Block
{
    public static final String[] field_150198_a = new String[] {"stone", "cobble", "brick", "mossybrick", "crackedbrick", "chiseledbrick"};
    private static final String __OBFID = "CL_00000271";

    public BlockSilverfish()
    {
        super(Material.field_151571_B);
        this.func_149711_c(0.0F);
        this.func_149647_a(CreativeTabs.tabDecorations);
    }

    @SideOnly(Side.CLIENT)
    public IIcon func_149691_a(int p_149691_1_, int p_149691_2_)
    {
        switch (p_149691_2_)
        {
            case 1:
                return Blocks.cobblestone.func_149733_h(p_149691_1_);
            case 2:
                return Blocks.stonebrick.func_149733_h(p_149691_1_);
            case 3:
                return Blocks.stonebrick.func_149691_a(p_149691_1_, 1);
            case 4:
                return Blocks.stonebrick.func_149691_a(p_149691_1_, 2);
            case 5:
                return Blocks.stonebrick.func_149691_a(p_149691_1_, 3);
            default:
                return Blocks.stone.func_149733_h(p_149691_1_);
        }
    }

    @SideOnly(Side.CLIENT)
    public void func_149651_a(IIconRegister p_149651_1_) {}

    public void func_149664_b(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_)
    {
        if (!p_149664_1_.isRemote)
        {
            EntitySilverfish entitysilverfish = new EntitySilverfish(p_149664_1_);
            entitysilverfish.setLocationAndAngles((double)p_149664_2_ + 0.5D, (double)p_149664_3_, (double)p_149664_4_ + 0.5D, 0.0F, 0.0F);
            p_149664_1_.spawnEntityInWorld(entitysilverfish);
            entitysilverfish.spawnExplosionParticle();
        }

        super.func_149664_b(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }

    public static boolean func_150196_a(Block p_150196_0_)
    {
        return p_150196_0_ == Blocks.stone || p_150196_0_ == Blocks.cobblestone || p_150196_0_ == Blocks.stonebrick;
    }

    public static int func_150195_a(Block p_150195_0_, int p_150195_1_)
    {
        if (p_150195_1_ == 0)
        {
            if (p_150195_0_ == Blocks.cobblestone)
            {
                return 1;
            }

            if (p_150195_0_ == Blocks.stonebrick)
            {
                return 2;
            }
        }
        else if (p_150195_0_ == Blocks.stonebrick)
        {
            switch (p_150195_1_)
            {
                case 1:
                    return 3;
                case 2:
                    return 4;
                case 3:
                    return 5;
            }
        }

        return 0;
    }

    public static ImmutablePair func_150197_b(int p_150197_0_)
    {
        switch (p_150197_0_)
        {
            case 1:
                return new ImmutablePair(Blocks.cobblestone, Integer.valueOf(0));
            case 2:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(0));
            case 3:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(1));
            case 4:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(2));
            case 5:
                return new ImmutablePair(Blocks.stonebrick, Integer.valueOf(3));
            default:
                return new ImmutablePair(Blocks.stone, Integer.valueOf(0));
        }
    }

    protected ItemStack func_149644_j(int p_149644_1_)
    {
        switch (p_149644_1_)
        {
            case 1:
                return new ItemStack(Blocks.cobblestone);
            case 2:
                return new ItemStack(Blocks.stonebrick);
            case 3:
                return new ItemStack(Blocks.stonebrick, 1, 1);
            case 4:
                return new ItemStack(Blocks.stonebrick, 1, 2);
            case 5:
                return new ItemStack(Blocks.stonebrick, 1, 3);
            default:
                return new ItemStack(Blocks.stone);
        }
    }

    public void func_149690_a(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_, int p_149690_5_, float p_149690_6_, int p_149690_7_)
    {
        if (!p_149690_1_.isRemote)
        {
            EntitySilverfish entitysilverfish = new EntitySilverfish(p_149690_1_);
            entitysilverfish.setLocationAndAngles((double)p_149690_2_ + 0.5D, (double)p_149690_3_, (double)p_149690_4_ + 0.5D, 0.0F, 0.0F);
            p_149690_1_.spawnEntityInWorld(entitysilverfish);
            entitysilverfish.spawnExplosionParticle();
        }
    }

    public int func_149643_k(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_)
    {
        return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
    }

    @SideOnly(Side.CLIENT)
    public void func_149666_a(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        for (int i = 0; i < field_150198_a.length; ++i)
        {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
        }
    }
}