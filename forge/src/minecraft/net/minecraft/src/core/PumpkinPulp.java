package net.minecraft.src.core;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;

public class PumpkinPulp extends ItemFood
    implements ITextureProvider
{
    public PumpkinPulp(int i, int j, boolean flag, int k)
    {
        super(i, j, flag);
        maxStackSize = 64;
    }

    public String getTextureFile()
    {
        return "/infitools/infifood.png";
    }
    
    /*public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 32;
    }*/
}
