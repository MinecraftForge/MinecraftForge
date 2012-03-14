package net.minecraft.src;

import net.minecraft.src.forge.ForgeHooks;

public class ItemHoe extends Item
{
    public ItemHoe(int par1, EnumToolMaterial par2EnumToolMaterial)
    {
        super(par1);
        this.maxStackSize = 1;
        this.setMaxDamage(par2EnumToolMaterial.getMaxUses());
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS !
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
    {
        if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6))
        {
            return false;
        }
        else
        {
            if (ForgeHooks.onUseHoe(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6))
            {
                par1ItemStack.damageItem(1, par2EntityPlayer);
                return true;
            }
            int var8 = par3World.getBlockId(par4, par5, par6);
            int var9 = par3World.getBlockId(par4, par5 + 1, par6);

            if ((par7 == 0 || var9 != 0 || var8 != Block.grass.blockID) && var8 != Block.dirt.blockID)
            {
                return false;
            }
            else
            {
                Block var10 = Block.tilledField;
                par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), var10.stepSound.getStepSound(), (var10.stepSound.getVolume() + 1.0F) / 2.0F, var10.stepSound.getPitch() * 0.8F);

                if (par3World.isRemote)
                {
                    return true;
                }
                else
                {
                    par3World.setBlockWithNotify(par4, par5, par6, var10.blockID);
                    par1ItemStack.damageItem(1, par2EntityPlayer);
                    return true;
                }
            }
        }
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    public boolean isFull3D()
    {
        return true;
    }
}
