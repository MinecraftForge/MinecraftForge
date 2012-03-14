package net.minecraft.src;

public class ItemSaddle extends Item
{
    public ItemSaddle(int par1)
    {
        super(par1);
        this.maxStackSize = 1;
    }

    /**
     * Called when a player right clicks a entity with a item.
     */
    public void useItemOnEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving)
    {
        if (par2EntityLiving instanceof EntityPig)
        {
            EntityPig var3 = (EntityPig)par2EntityLiving;

            if (!var3.getSaddled())
            {
                var3.setSaddled(true);
                --par1ItemStack.stackSize;
            }
        }
    }

    public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
    {
        this.useItemOnEntity(par1ItemStack, par2EntityLiving);
        return true;
    }
}
