package net.minecraft.src.orizon;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class OreReplacementMetalItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "Iron", "Iron", "Iron", "Iron", "Gold", "Gold", "Gold", "Gold"
    };

    public OreReplacementMetalItem(int i)
    {
        super(i);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int md)
    {
        return md;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append("ore").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
