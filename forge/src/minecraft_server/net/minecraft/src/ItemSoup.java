package net.minecraft.src;

public class ItemSoup extends ItemFood
{
    public ItemSoup(int par1, int par2)
    {
        super(par1, par2, false);
        this.setMaxStackSize(1);
    }

    public ItemStack onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        super.onFoodEaten(par1ItemStack, par2World, par3EntityPlayer);
        return new ItemStack(Item.bowlEmpty);
    }
}
