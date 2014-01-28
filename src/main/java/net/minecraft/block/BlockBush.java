package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import static net.minecraftforge.common.EnumPlantType.*;

public class BlockBush extends Block implements IPlantable
{
    private static final String __OBFID = "CL_00000208";

    protected BlockBush(Material p_i45395_1_)
    {
        super(p_i45395_1_);
        this.func_149675_a(true);
        float f = 0.2F;
        this.func_149676_a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
        this.func_149647_a(CreativeTabs.tabDecorations);
    }

    protected BlockBush()
    {
        this(Material.field_151585_k);
    }

    public boolean func_149742_c(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
    {
        return super.func_149742_c(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_) && this.func_149718_j(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
    }

    protected boolean func_149854_a(Block p_149854_1_)
    {
        return p_149854_1_ == Blocks.grass || p_149854_1_ == Blocks.dirt || p_149854_1_ == Blocks.farmland;
    }

    public void func_149695_a(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        super.func_149695_a(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
        this.func_149855_e(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
    }

    public void func_149674_a(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        this.func_149855_e(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
    }

    protected void func_149855_e(World p_149855_1_, int p_149855_2_, int p_149855_3_, int p_149855_4_)
    {
        if (!this.func_149718_j(p_149855_1_, p_149855_2_, p_149855_3_, p_149855_4_))
        {
            this.func_149697_b(p_149855_1_, p_149855_2_, p_149855_3_, p_149855_4_, p_149855_1_.getBlockMetadata(p_149855_2_, p_149855_3_, p_149855_4_), 0);
            p_149855_1_.func_147465_d(p_149855_2_, p_149855_3_, p_149855_4_, func_149729_e(0), 0, 2);
        }
    }

    public boolean func_149718_j(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
    {
        return  p_149718_1_.func_147439_a(p_149718_2_, p_149718_3_ - 1, p_149718_4_).canSustainPlant(p_149718_1_, p_149718_2_, p_149718_3_ - 1, p_149718_4_, ForgeDirection.UP, this);
    }

    public AxisAlignedBB func_149668_a(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }

    public boolean func_149662_c()
    {
        return false;
    }

    public boolean func_149686_d()
    {
        return false;
    }

    public int func_149645_b()
    {
        return 1;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
    {
        if (this == Blocks.wheat)          return Crop;
        if (this == Blocks.carrots)        return Crop;
        if (this == Blocks.potatoes)       return Crop;
        if (this == Blocks.melon_stem)     return Crop;
        if (this == Blocks.pumpkin_stem)   return Crop;
        if (this == Blocks.deadbush)       return Desert;
        if (this == Blocks.waterlily)      return Water;
        if (this == Blocks.red_mushroom)   return Cave;
        if (this == Blocks.brown_mushroom) return Cave;
        if (this == Blocks.nether_wart)    return Nether;
        if (this == Blocks.sapling)        return Plains;
        if (this == Blocks.tallgrass)      return Plains;
        if (this == Blocks.double_plant)   return Plains;
        if (this == Blocks.red_flower)     return Plains;
        if (this == Blocks.yellow_flower)  return Plains;        
        return Plains;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z)
    {
        return this;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }
}