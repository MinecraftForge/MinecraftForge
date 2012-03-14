package net.minecraft.src;

public class ItemSaddle extends Item
{
    public ItemSaddle(int par1)
    {
        super(par1);
        this.maxStackSize = 1;
    }

    /**
     * Called from ItemStack.useItemOnEntity
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

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving, EntityLiving par3EntityLiving)
    {
        this.useItemOnEntity(par1ItemStack, par2EntityLiving);
        return true;
    }
}
