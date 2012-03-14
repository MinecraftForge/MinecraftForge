package net.minecraft.src.blocks;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.*;
import net.minecraft.src.forge.*;
import net.minecraft.client.Minecraft;

public class BrickBlockItem extends ItemBlock
{	
	public static final String blockType[] =
	{
	    "obsidian", "snow", "sandstone", "brick", "netherrack", "diamond", "gold", "lapis", 
	    "slab", "stoneSmall", "slabSmall", "brickTile", "iron", "redstone", "slime", "bone"
	};

    public BrickBlockItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.brick.blockID, this);
    }
    
    public int getMetadata(int i)
    {
        return i;
    }

    public int getIconFromDamage(int i)
    {
        return mod_InfiBlocks.brick.getBlockTextureFromSideAndMetadata(0, i);
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("Brick").toString();
    }
}
