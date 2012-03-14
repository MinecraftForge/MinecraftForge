package net.minecraft.src.blocks;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class MagicSlabSoilItem extends ItemBlock
{
    public static final String blockType[] =
    {
    	"dirt", "grass", "mycelium", "sand", "gravel", "oak", "pine", "birch", 
    	"planks", "snow", "soulsand", "brownMushroom", "redMushroom", "glowstone", "glass", "oakLeaves"
    };

    public MagicSlabSoilItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_InfiBlocks.magicSlabSoil.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("MagicSlab").toString();
    }
}
