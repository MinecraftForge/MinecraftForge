package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class RecipeItem extends Item
    implements ITextureProvider
{
    public String texturePath;
    public static final String blockType[] =
    {
        "barley", "barleyflour"
    };

    public RecipeItem(int i, String s)
    {
        super(i);
        texturePath = s;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public String getTextureFile()
    {
        return texturePath;
    }

    public int getMetadata(int i)
    {
        return i;
    }

    public int getIconFromDamage(int i)
    {
        return iconIndex + i;
    }

    public String getItemNameIS(ItemStack itemstack)
    {
        return (new StringBuilder()).append(blockType[itemstack.getItemDamage()]).append("Flora").toString();
    }
}
