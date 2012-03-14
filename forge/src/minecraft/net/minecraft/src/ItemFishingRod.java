package net.minecraft.src;

public class ItemFishingRod extends Item
{
    public ItemFishingRod(int par1)
    {
        super(par1);
        this.setMaxDamage(64);
        this.setMaxStackSize(1);
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * Returns true if this item should be rotated by 180 degrees around the Y axis when being held in an entities
     * hands.
     */
    public boolean shouldRotateAroundWhenRendering()
    {
        return true;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        if (par3EntityPlayer.fishEntity != null)
        {
            int var4 = par3EntityPlayer.fishEntity.catchFish();
            par1ItemStack.damageItem(var4, par3EntityPlayer);
            par3EntityPlayer.swingItem();
        }
        else
        {
            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!par2World.isRemote)
            {
                par2World.spawnEntityInWorld(new EntityFishHook(par2World, par3EntityPlayer));
            }

            par3EntityPlayer.swingItem();
        }

        return par1ItemStack;
    }
}
