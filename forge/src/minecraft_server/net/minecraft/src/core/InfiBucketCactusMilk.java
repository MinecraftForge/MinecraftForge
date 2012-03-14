package net.minecraft.src.core;
import net.minecraft.src.*;
import net.minecraft.src.forge.ITextureProvider;

public class InfiBucketCactusMilk extends Item
	implements ITextureProvider
{
    public InfiBucketCactusMilk(int i)
    {
        super(i);
        setMaxStackSize(1);
    }

    public ItemStack onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        itemstack.stackSize--;
        if (!world.isRemote)
        {
            entityplayer.clearActivePotions();
        }
        if (itemstack.stackSize <= 0)
        {
            return new ItemStack(mod_InfiTools.cactusBucketEmpty);
        }
        else
        {
            return itemstack;
        }
    }

    public int getMaxItemUseDuration(ItemStack itemstack)
    {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.drink;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        return itemstack;
    }
    
    public String getTextureFile()
    {
        return "/infitools/infitems.png";
    }
}
