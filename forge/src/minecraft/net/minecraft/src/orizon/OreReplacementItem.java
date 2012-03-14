package net.minecraft.src.orizon;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class OreReplacementItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "Coal", "Coal", "Coal", "Coal", "Diamond", "Diamond", "Diamond", "Diamond", 
        "Lapis", "Lapis", "Lapis", "Lapis", "Redstone", "Redstone", "Redstone", "Redstone"
    };

    public OreReplacementItem(int i)
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
