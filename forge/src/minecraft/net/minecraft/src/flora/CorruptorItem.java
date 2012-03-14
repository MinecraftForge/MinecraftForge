package net.minecraft.src.flora;

import net.minecraft.src.*;
import net.minecraft.src.forge.MinecraftForgeClient;

public class CorruptorItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "blood", "azure", "envy", "amber", "heart", "dark", "bright", "pure", "bloodgrass", "azuregrass",
        "envygrass", "ambergrass", "heartgrass", "darkgrass", "brightgrass", "puregrass"
    };

    public CorruptorItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
        //MinecraftForgeClient.registerCustomItemRenderer(mod_FloraSoma.corruptor.blockID, this);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("corruptor").toString();
    }
}
