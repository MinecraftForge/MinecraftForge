package net.minecraft.src.blocks;

import net.minecraft.src.*;

public class BrickFancyMagicSlabItem extends ItemBlock
{
	public static final String blockType[] =
		{
		    "obsidian", "snow", "sandstone", "brick", "netherrack", "diamond", "gold", "lapis", 
		    "slab", "stone", "", "brickTile", "iron", "redstone", "slime", "bone"
		};

    public BrickFancyMagicSlabItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.fancyBrickMagicSlab.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("FancyBrickMagicSlab").toString();
    }
}
