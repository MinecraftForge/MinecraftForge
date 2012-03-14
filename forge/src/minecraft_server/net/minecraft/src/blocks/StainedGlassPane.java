package net.minecraft.src.blocks;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.ArrayList;
import java.util.Random;

public class StainedGlassPane extends PaneBase
	implements ITextureProvider
{

    public StainedGlassPane(int i, int j, int k, Material material)
    {
        super(i, j, k, material);
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
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 5));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 6));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 7));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 8));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 9));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 10));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 11));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 12));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 13));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 14));
        arraylist.add(new ItemStack(mod_InfiBlocks.stainedGlassPane, 1, 15));
    }
}
