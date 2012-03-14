package net.minecraft.src.blocks;

import net.minecraft.src.*;

public class BrickIceMagicSlabItem extends ItemBlock
{
	public static final String blockType[] =
		{
		    "ice", "smallIce", "fancyIce"
		};

    public BrickIceMagicSlabItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.iceBrickMagicSlab.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("BrickMagicSlab").toString();
    }
}
