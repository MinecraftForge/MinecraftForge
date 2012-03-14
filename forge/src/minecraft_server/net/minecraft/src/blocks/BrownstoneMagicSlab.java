package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class BrownstoneMagicSlab extends MagicSlabBase
	implements ITextureProvider
{
    public BrownstoneMagicSlab(int i, int j)
    {
        super(i, j, Material.rock);
    }
    
    public void onEntityWalking(World world, int i, int j, int k, Entity entity)
    {
        double d = Math.abs(entity.motionX);
        double d1 = Math.abs(entity.motionZ);
        if(d < 0.3D)
        {
            entity.motionX *= 2.2D;
        }
        if(d1 < 0.3D)
        {
            entity.motionZ *= 2.2D;
        }
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture+md;
    }
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.brownstoneMagicSlab, 1, 5));
    }
}
