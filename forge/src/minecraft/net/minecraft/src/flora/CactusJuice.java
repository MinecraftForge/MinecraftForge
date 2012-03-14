package net.minecraft.src.flora;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;

public class CactusJuice extends ItemFood
    implements ITextureProvider
{
    public CactusJuice(int i, int j, boolean flag, int k)
    {
        super(i, j, flag);
        maxStackSize = 64;
    }

    public String getTextureFile()
    {
        return "/floratex/infifood.png";
    }
    
    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 12;
    }
}
