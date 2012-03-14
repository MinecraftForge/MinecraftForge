package net.minecraft.src.orizon;
import net.minecraft.src.forge.*;
import net.minecraft.src.*;

public class MarbleItem extends ItemBlock
{
    public static final String blockType[] =
    {
        "white", "black", "rose", "emerald", "azure", "ruby"
    };

    public MarbleItem(int i)
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
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("Marble").toString();
    }
}
