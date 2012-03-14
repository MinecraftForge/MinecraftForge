package net.minecraft.src.blocks;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class StainedGlassMagicSlab extends MagicSlabBase
	implements ITextureProvider
{
    public StainedGlassMagicSlab(int i, int j)
    {
        super(i, j, Material.rock);
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }
    
    public int idDropped(int i, Random random, int j)
    {
    	return 0;
    }
    
    public int getRenderBlockPass()
    {
        return 1;
    }
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 5));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 6));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 7));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 8));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 9));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 10));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 11));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 12));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 13));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 14));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassMagicSlab, 1, 15));
    }
}
