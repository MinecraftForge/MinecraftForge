package net.minecraft.src.blocks;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class InfiGlassMagicSlab extends MagicSlabBase
	implements ITextureProvider
{
    public InfiGlassMagicSlab(int i, int j)
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
    
    /*public int getRenderBlockPass()
    {
        return 1;
    }*/
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlassMagicSlab, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlassMagicSlab, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlassMagicSlab, 1, 2));
    }
}
