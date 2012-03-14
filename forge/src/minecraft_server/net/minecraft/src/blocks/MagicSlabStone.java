package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;

public class MagicSlabStone extends MagicSlabBase
{
    public MagicSlabStone(int i, int j)
    {
        super(i, j, Material.rock);
    }
    
    public float getHardness(int md) {
    	switch(md) {
    	case 0: return Block.stone.getHardness(0);
        case 1: return Block.stairSingle.getHardness(0);
        case 2: return Block.cobblestone.getHardness(0);
        case 3: return Block.stoneBrick.getHardness(0);
        case 4: return Block.stoneBrick.getHardness(1);
        case 5: return Block.stoneBrick.getHardness(2);
        case 6: return Block.brick.getHardness(0);
        case 7: return Block.cobblestoneMossy.getHardness(0);
        case 8: return Block.obsidian.getHardness(0);
        case 9: return Block.netherrack.getHardness(0);
        case 10: return Block.sandStone.getHardness(0);
        case 11: return Block.blockSteel.getHardness(0);
        case 12: return Block.blockGold.getHardness(0);
        case 13: return Block.blockDiamond.getHardness(0);
        case 14: return Block.whiteStone.getHardness(0);
        case 15: return Block.netherBrick.getHardness(0);
        default: return 0;
    	}
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        switch(md)
        {
        case 0: return Block.stone.getBlockTextureFromSideAndMetadata(side, 0);
        case 1: return Block.stairSingle.getBlockTextureFromSideAndMetadata(side, 0);
        case 2: return Block.cobblestone.getBlockTextureFromSideAndMetadata(side, 0);
        case 3: return Block.stoneBrick.getBlockTextureFromSideAndMetadata(side, 0);
        case 4: return Block.stoneBrick.getBlockTextureFromSideAndMetadata(side, 1);
        case 5: return Block.stoneBrick.getBlockTextureFromSideAndMetadata(side, 2);
        case 6: return Block.brick.getBlockTextureFromSideAndMetadata(side, 0);
        case 7: return Block.cobblestoneMossy.getBlockTextureFromSideAndMetadata(side, 0);
        case 8: return Block.obsidian.getBlockTextureFromSideAndMetadata(side, 0);
        case 9: return Block.netherrack.getBlockTextureFromSideAndMetadata(side, 0);
        case 10: return Block.sandStone.getBlockTextureFromSideAndMetadata(side, 0);
        case 11: return Block.blockSteel.getBlockTextureFromSideAndMetadata(side, 0);
        case 12: return Block.blockGold.getBlockTextureFromSideAndMetadata(side, 0);
        case 13: return Block.blockDiamond.getBlockTextureFromSideAndMetadata(side, 0);
        case 14: return Block.whiteStone.getBlockTextureFromSideAndMetadata(side, 0);
        case 15: return Block.netherBrick.getBlockTextureFromSideAndMetadata(side, 0);
        default: return 0;
        }
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 5));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 6));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 7));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 8));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 9));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 10));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 11));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 12));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 13));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 14));
        arraylist.add(new ItemStack(mod_InfiBlocks.magicSlabStone, 1, 15));
    }
}
