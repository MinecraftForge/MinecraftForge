package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class BrickIceMagicSlab extends MagicSlabBase
	implements ITextureProvider
{
    public BrickIceMagicSlab(int i, int j)
    {
        super(i, j, Material.ice);
    }
    
    public int getRenderBlockPass()
    {
        return 1;
    }
    
    public int getMobilityFlag()
    {
        return 0;
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
        arraylist.add(new ItemStack(mod_InfiBlocks.iceBrickMagicSlab, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.iceBrickMagicSlab, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.iceBrickMagicSlab, 1, 2));
    }
}
