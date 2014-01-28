package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIce extends BlockBreakable
{
    private static final String __OBFID = "CL_00000259";

    public BlockIce()
    {
        super("ice", Material.field_151588_w, false);
        this.field_149765_K = 0.98F;
        this.func_149675_a(true);
        this.func_149647_a(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public int func_149701_w()
    {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public boolean func_149646_a(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return super.func_149646_a(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, 1 - p_149646_5_);
    }

    public void func_149636_a(World p_149636_1_, EntityPlayer p_149636_2_, int p_149636_3_, int p_149636_4_, int p_149636_5_, int p_149636_6_)
    {
        p_149636_2_.addStat(StatList.mineBlockStatArray[Block.func_149682_b(this)], 1);
        p_149636_2_.addExhaustion(0.025F);

        if (this.func_149700_E() && EnchantmentHelper.getSilkTouchModifier(p_149636_2_))
        {
            ItemStack itemstack = this.func_149644_j(p_149636_6_);

            if (itemstack != null)
            {
                this.func_149642_a(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, itemstack);
            }
        }
        else
        {
            if (p_149636_1_.provider.isHellWorld)
            {
                p_149636_1_.func_147468_f(p_149636_3_, p_149636_4_, p_149636_5_);
                return;
            }

            int i1 = EnchantmentHelper.getFortuneModifier(p_149636_2_);
            this.func_149697_b(p_149636_1_, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_, i1);
            Material material = p_149636_1_.func_147439_a(p_149636_3_, p_149636_4_ - 1, p_149636_5_).func_149688_o();

            if (material.blocksMovement() || material.isLiquid())
            {
                p_149636_1_.func_147449_b(p_149636_3_, p_149636_4_, p_149636_5_, Blocks.flowing_water);
            }
        }
    }

    public int func_149745_a(Random p_149745_1_)
    {
        return 0;
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        if (p_149674_1_.getSavedLightValue(EnumSkyBlock.Block, p_149674_2_, p_149674_3_, p_149674_4_) > 11 - this.func_149717_k())
        {
            if (p_149674_1_.provider.isHellWorld)
            {
                p_149674_1_.func_147468_f(p_149674_2_, p_149674_3_, p_149674_4_);
                return;
            }

            this.func_149697_b(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_1_.getBlockMetadata(p_149674_2_, p_149674_3_, p_149674_4_), 0);
            p_149674_1_.func_147449_b(p_149674_2_, p_149674_3_, p_149674_4_, Blocks.water);
        }
    }

    public int func_149656_h()
    {
        return 0;
    }
}