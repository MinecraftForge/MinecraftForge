package net.minecraft.src.blocks;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class BrickIce extends BlockBreakable
	implements ITextureProvider
{
    public BrickIce(int i, int j)
    {
        super(i, j, Material.ice, false);
    }
    
    public int getRenderBlockPass()
    {
        return 1;
    }
    
    public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
        return super.shouldSideBeRendered(iblockaccess, i, j, k, 1 - l);
    }
    
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l)
    {
        super.harvestBlock(world, entityplayer, i, j, k, l);
        Material material = world.getBlockMaterial(i, j - 1, k);
        if (material.blocksMovement() || material.isLiquid())
        {
            world.setBlockWithNotify(i, j, k, Block.waterMoving.blockID);
        }
    }
    
    public int quantityDropped(Random random)
    {
        return 0;
    }
    
    public void updateTick(World world, int i, int j, int k, Random random)
    {
        if (world.getSavedLightValue(EnumSkyBlock.Block, i, j, k) > 11 - Block.lightOpacity[blockID])
        {
            dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, Block.waterStill.blockID);
        }
    }

    public int getMobilityFlag()
    {
        return 0;
    }

    protected ItemStack createStackedBlock(int i)
    {
        return null;
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.iceBrick, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.iceBrick, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.iceBrick, 1, 2));
    }
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
}
