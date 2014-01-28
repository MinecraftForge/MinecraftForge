package net.minecraft.world.chunk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk
{
    private static final String __OBFID = "CL_00000372";

    public EmptyChunk(World par1World, int par2, int par3)
    {
        super(par1World, par2, par3);
    }

    // JAVADOC METHOD $$ func_76600_a
    public boolean isAtLocation(int par1, int par2)
    {
        return par1 == this.xPosition && par2 == this.zPosition;
    }

    // JAVADOC METHOD $$ func_76611_b
    public int getHeightValue(int par1, int par2)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_76603_b
    public void generateSkylightMap() {}

    // JAVADOC METHOD $$ func_76590_a
    @SideOnly(Side.CLIENT)
    public void generateHeightMap() {}

    public Block func_150810_a(int p_150810_1_, int p_150810_2_, int p_150810_3_)
    {
        return Blocks.air;
    }

    public int func_150808_b(int p_150808_1_, int p_150808_2_, int p_150808_3_)
    {
        return 255;
    }

    public boolean func_150807_a(int p_150807_1_, int p_150807_2_, int p_150807_3_, Block p_150807_4_, int p_150807_5_)
    {
        return true;
    }

    // JAVADOC METHOD $$ func_76628_c
    public int getBlockMetadata(int par1, int par2, int par3)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_76589_b
    public boolean setBlockMetadata(int par1, int par2, int par3, int par4)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_76614_a
    public int getSavedLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_76633_a
    public void setLightValue(EnumSkyBlock par1EnumSkyBlock, int par2, int par3, int par4, int par5) {}

    // JAVADOC METHOD $$ func_76629_c
    public int getBlockLightValue(int par1, int par2, int par3, int par4)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_76612_a
    public void addEntity(Entity par1Entity) {}

    // JAVADOC METHOD $$ func_76622_b
    public void removeEntity(Entity par1Entity) {}

    // JAVADOC METHOD $$ func_76608_a
    public void removeEntityAtIndex(Entity par1Entity, int par2) {}

    // JAVADOC METHOD $$ func_76619_d
    public boolean canBlockSeeTheSky(int par1, int par2, int par3)
    {
        return false;
    }

    public TileEntity func_150806_e(int p_150806_1_, int p_150806_2_, int p_150806_3_)
    {
        return null;
    }

    public void func_150813_a(TileEntity p_150813_1_) {}

    public void func_150812_a(int p_150812_1_, int p_150812_2_, int p_150812_3_, TileEntity p_150812_4_) {}

    public void func_150805_f(int p_150805_1_, int p_150805_2_, int p_150805_3_) {}

    // JAVADOC METHOD $$ func_76631_c
    public void onChunkLoad() {}

    // JAVADOC METHOD $$ func_76623_d
    public void onChunkUnload() {}

    // JAVADOC METHOD $$ func_76630_e
    public void setChunkModified() {}

    // JAVADOC METHOD $$ func_76588_a
    public void getEntitiesWithinAABBForEntity(Entity par1Entity, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector) {}

    // JAVADOC METHOD $$ func_76618_a
    public void getEntitiesOfTypeWithinAAAB(Class par1Class, AxisAlignedBB par2AxisAlignedBB, List par3List, IEntitySelector par4IEntitySelector) {}

    // JAVADOC METHOD $$ func_76601_a
    public boolean needsSaving(boolean par1)
    {
        return false;
    }

    public Random getRandomWithSeed(long par1)
    {
        return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ par1);
    }

    public boolean isEmpty()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_76606_c
    public boolean getAreLevelsEmpty(int par1, int par2)
    {
        return true;
    }
}