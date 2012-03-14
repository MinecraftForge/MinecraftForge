package net.minecraft.src.blocks;
import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import java.util.*;

public class InfiGlassPane extends PaneBase
	implements ITextureProvider
{

    public InfiGlassPane(int i, int j, int k, Material material)
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
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlassPane, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlassPane, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.infiGlassPane, 1, 2));
    }
}
