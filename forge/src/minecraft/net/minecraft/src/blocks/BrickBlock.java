package net.minecraft.src.blocks;
import java.util.ArrayList;

import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class BrickBlock extends Block
	implements ITextureProvider
{
    public BrickBlock(int i, int j)
    {
        super(i, j, Material.rock);
    }
    
    public float getHardness(int md) {
    	switch(md) {
    	case 0: return Block.obsidian.getHardness(0);
        case 1: return 0.5F; //Snow
        case 2: return 1.2F; //Sandstone
        case 3: return Block.brick.getHardness(0);
        case 4: return 1.5F; //Netherrack
        case 5: return Block.blockDiamond.getHardness(0);
        case 6: return Block.blockGold.getHardness(0);
        case 7: return Block.blockLapis.getHardness(0);
        case 8: return Block.stone.getHardness(0);
        case 9: return Block.stone.getHardness(0);
        case 10: return Block.stone.getHardness(0);
        case 11: return Block.brick.getHardness(0);
        case 12: return Block.blockSteel.getHardness(0);
        case 13: return 3F;
        case 14: return 3F;
        case 15: return 3F;
        default: return 0;
    	}
    }
    
    protected int damageDropped(int md)
    {
        return md;
    }
    
    public int getBlockTextureFromSideAndMetadata(int side, int md)
    {
        return blockIndexInTexture + md;
    }
    
    public void addCreativeItems(ArrayList arraylist)
    {
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 0));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 1));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 2));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 3));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 4));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 5));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 6));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 7));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 8));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 9));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 10));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 11));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 12));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 13));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 14));
        arraylist.add(new ItemStack(mod_InfiBlocks.brick, 1, 15));
    }
    
    public String getTextureFile()
    {
        return "/infiblocks/infiblocks.png";
    }
}
