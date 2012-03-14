package net.minecraft.src.core;

import net.minecraft.src.forge.ITextureProvider;
import net.minecraft.src.*;

public class InfiFoodBowl extends ItemFood
    implements ITextureProvider
{
    private int itemID;

    public InfiFoodBowl(int i, int j)
    {
        super(i, j, false);
        setMaxStackSize(1);
    }

    public InfiFoodBowl(int i, int j, int k)
    {
        super(i, j, false);
        itemID = k;
        setMaxStackSize(1);
    }

    public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        super.onFoodEaten(itemstack, world, entityplayer);
        if (itemID == 1)
        {
            return new ItemStack(Item.bowlEmpty, 1, 0);
        }
        if (itemID == 2)
        {
            return new ItemStack(mod_InfiTools.stoneBowlEmpty, 1, 0);
        }
        if (itemID == 3)
        {
            return new ItemStack(mod_InfiTools.ironBowlEmpty, 1, 0);
        }
        if (itemID == 4)
        {
            return new ItemStack(mod_InfiTools.goldBowlEmpty, 1, 0);
        }
        if (itemID == 5)
        {
            return new ItemStack(mod_InfiTools.netherrackBowlEmpty, 1, 0);
        }
        if (itemID == 6)
        {
            return new ItemStack(mod_InfiTools.slimeBowlEmpty, 1, 0);
        }
        if (itemID == 7)
        {
            return new ItemStack(mod_InfiTools.cactusBowlEmpty, 1, 0);
        }
        if (itemID == 8)
        {
            return new ItemStack(mod_InfiTools.glassBowlEmpty, 1, 0);
        }
        else
        {
            return new ItemStack(Item.appleRed, 1, 0);
        }
    }

    public String getTextureFile()
    {
        return "/infitools/infifood.png";
    }
}
