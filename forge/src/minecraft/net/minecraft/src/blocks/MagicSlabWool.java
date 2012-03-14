package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;

public class MagicSlabWool extends MagicSlabBase
{
    public MagicSlabWool(int i, int j)
    {
        super(i, j, Material.cloth);
    }
    
    public int getBlockTextureFromSideAndMetadata(int i, int j)
    {
        if (j == 0)
        {
            return blockIndexInTexture;
        }
        else
        {
            j = ~(j & 0xf);
            return 113 + ((j & 8) >> 3) + (j & 7) * 16;
        }
    }
    
    public static int getBlockFromDye(int i)
    {
        return ~i & 0xf;
    }

    public static int getDyeFromBlock(int i)
    {
        return ~i & 0xf;
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 5));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 6));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 7));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 8));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 9));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 10));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 11));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 12));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 13));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 14));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabWool, 1, 15));
    }
}
