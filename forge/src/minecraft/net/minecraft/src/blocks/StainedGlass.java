package net.minecraft.src.blocks;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;

import java.util.ArrayList;
import java.util.Random;

public class StainedGlass extends BlockBreakable
	implements ITextureProvider
{
    public StainedGlass(int i, int j, Material material, boolean flag)
    {
        super(i, j, material, flag);
    }
    
    public int idDropped(int i, Random random, int j)
    {
    	return 0;
    }

    public int getRenderBlockPass()
    {
        return 1;
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 5));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 6));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 7));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 8));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 9));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 10));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 11));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 12));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 13));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 14));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlass, 1, 15));
    }
}
