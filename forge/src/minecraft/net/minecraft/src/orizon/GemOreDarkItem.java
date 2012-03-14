package net.minecraft.src.orizon;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class GemOreDarkItem extends ItemBlock
{
    public GemOreDarkItem(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }
    
    public static final String blockType[] =
    {
        "Ruby", "Emerald", "Sapphire", "Topaz", "Amethyst", "Quartz", "RoseQuartz", "RockCrystal"
    };

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append("gem").append(blockType[itemstack.getItemDamage()])
        		.append("Ore").toString();
    }
}
