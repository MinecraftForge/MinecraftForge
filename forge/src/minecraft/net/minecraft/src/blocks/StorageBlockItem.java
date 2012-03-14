package net.minecraft.src.blocks;

import net.minecraft.src.*;
import net.minecraft.src.forge.ICustomItemRenderer;
import net.minecraft.src.forge.MinecraftForgeClient;

public class StorageBlockItem extends ItemBlock
{
	public static final String blockType[] =
	{
	    "coal", "charcoal", "redstone", "slime", "bone", "wheat", "dirt", "", "", "", "", "",
	    "netherrack", "sandstone", "slab", "brick"
	};

    public StorageBlockItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.storageBlock.blockID, this);
    }

    public int getIconFromDamage(int i)
    {
        return mod_InfiBlocks.storageBlock.getBlockTextureFromSideAndMetadata(0, i);
    }

    public int getMetadata(int i)
    {
        return i;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("Storage").toString();
    }
}
