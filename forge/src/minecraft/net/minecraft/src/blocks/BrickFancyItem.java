package net.minecraft.src.blocks;

import net.minecraft.src.*;
import net.minecraft.src.forge.*;

public class BrickFancyItem extends ItemBlock
{
	public static final String blockType[] =
	{
	    "obsidian", "snow", "sandstone", "brick", "netherrack", "diamond", "gold", "lapis", 
	    "slab", "stone", "", "brickTile", "iron", "redstone", "slime", "bone"
	};

    public BrickFancyItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.fancyBrick.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("FancyBrick").toString();
    }
}
