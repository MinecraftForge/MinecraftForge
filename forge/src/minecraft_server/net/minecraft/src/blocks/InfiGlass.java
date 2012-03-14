package net.minecraft.src.blocks;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;

import java.util.ArrayList;
import java.util.Random;

public class InfiGlass extends BlockBreakable
	implements ITextureProvider
{
    public InfiGlass(int i, int j, Material material, boolean flag)
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
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlass, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlass, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlass, 1, 2));
    }
}
