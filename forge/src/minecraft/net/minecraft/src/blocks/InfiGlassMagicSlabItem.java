package net.minecraft.src.blocks;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class InfiGlassMagicSlabItem extends ItemBlock
{
	public static final String blockType[] =
	    {
	        "clear", "soul", "clearSoul"
	    };

    public InfiGlassMagicSlabItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.infiGlassMagicSlab.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("GlassMagicSlab").toString();
    }
}
