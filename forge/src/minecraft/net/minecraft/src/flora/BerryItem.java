package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;

public class BerryItem extends ItemFood
    implements ITextureProvider
{
    public static final String blockType[] =
    {
        "rasp", "blue", "black", "geo"
    };

    public BerryItem(int i, int j)
    {
        super(i, j, 0.1F, false);
        setHasSubtypes(true);
        setMaxDamage(0);
        iconIndex = 16;
    }

    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 12;
    }

    public int getIconFromDamage(int i)
    {
        return iconIndex + i;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("berry").toString();
    }

    public String getTextureFile()
    {
        return "/floratex/seeds.png";
    }
}
