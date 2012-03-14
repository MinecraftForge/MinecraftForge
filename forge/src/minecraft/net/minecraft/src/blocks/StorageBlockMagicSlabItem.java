package net.minecraft.src.blocks;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class StorageBlockMagicSlabItem extends ItemBlock
{
	public static final String blockType[] =
	{
	    "coal", "charcoal", "redstone", "slime", "bone", "wheat", "dirt", "", "", "", "", "",
	    "netherrack", "sandstone", "slab", "brick"
	};

    public StorageBlockMagicSlabItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.storageBlockMagicSlab.blockID, this);
    }

    public int getIconFromDamage(int i)
    {
        return mod_InfiBlocks.storageBlockMagicSlab.getBlockTextureFromSideAndMetadata(0, i);
    }
    
    public int getMetadata(int i)
    {
        return i;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("StorageMagicSlab").toString();
    }
}
