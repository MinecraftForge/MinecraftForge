package net.minecraft.src.orizon;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class MineralOreLowItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "Copper", "Turquoise", "Chalcocte", "Cassiterite", "Teallite", "ZincBloom", 
        "Sphalerite", "Cerussite", "Cobalt", "Ardite", "Myuvil", "Galena", "Chrysocolla", "Ivymetal"
    };

    public MineralOreLowItem(int i)
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
        return (new StringBuilder()).append("mineral").append(blockType[itemstack.getItemDamage()]).toString();
    }
}
