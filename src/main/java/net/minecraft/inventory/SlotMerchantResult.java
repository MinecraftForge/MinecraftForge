package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot
{
    // JAVADOC FIELD $$ field_75233_a
    private final InventoryMerchant theMerchantInventory;
    // JAVADOC FIELD $$ field_75232_b
    private EntityPlayer thePlayer;
    private int field_75231_g;
    // JAVADOC FIELD $$ field_75234_h
    private final IMerchant theMerchant;
    private static final String __OBFID = "CL_00001758";

    public SlotMerchantResult(EntityPlayer par1EntityPlayer, IMerchant par2IMerchant, InventoryMerchant par3InventoryMerchant, int par4, int par5, int par6)
    {
        super(par3InventoryMerchant, par4, par5, par6);
        this.thePlayer = par1EntityPlayer;
        this.theMerchant = par2IMerchant;
        this.theMerchantInventory = par3InventoryMerchant;
    }

    // JAVADOC METHOD $$ func_75214_a
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }

    // JAVADOC METHOD $$ func_75209_a
    public ItemStack decrStackSize(int par1)
    {
        if (this.getHasStack())
        {
            this.field_75231_g += Math.min(par1, this.getStack().stackSize);
        }

        return super.decrStackSize(par1);
    }

    // JAVADOC METHOD $$ func_75210_a
    protected void onCrafting(ItemStack par1ItemStack, int par2)
    {
        this.field_75231_g += par2;
        this.onCrafting(par1ItemStack);
    }

    // JAVADOC METHOD $$ func_75208_c
    protected void onCrafting(ItemStack par1ItemStack)
    {
        par1ItemStack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
        this.field_75231_g = 0;
    }

    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
        this.onCrafting(par2ItemStack);
        MerchantRecipe merchantrecipe = this.theMerchantInventory.getCurrentRecipe();

        if (merchantrecipe != null)
        {
            ItemStack itemstack1 = this.theMerchantInventory.getStackInSlot(0);
            ItemStack itemstack2 = this.theMerchantInventory.getStackInSlot(1);

            if (this.func_75230_a(merchantrecipe, itemstack1, itemstack2) || this.func_75230_a(merchantrecipe, itemstack2, itemstack1))
            {
                this.theMerchant.useRecipe(merchantrecipe);

                if (itemstack1 != null && itemstack1.stackSize <= 0)
                {
                    itemstack1 = null;
                }

                if (itemstack2 != null && itemstack2.stackSize <= 0)
                {
                    itemstack2 = null;
                }

                this.theMerchantInventory.setInventorySlotContents(0, itemstack1);
                this.theMerchantInventory.setInventorySlotContents(1, itemstack2);
            }
        }
    }

    private boolean func_75230_a(MerchantRecipe par1MerchantRecipe, ItemStack par2ItemStack, ItemStack par3ItemStack)
    {
        ItemStack itemstack2 = par1MerchantRecipe.getItemToBuy();
        ItemStack itemstack3 = par1MerchantRecipe.getSecondItemToBuy();

        if (par2ItemStack != null && par2ItemStack.getItem() == itemstack2.getItem())
        {
            if (itemstack3 != null && par3ItemStack != null && itemstack3.getItem() == par3ItemStack.getItem())
            {
                par2ItemStack.stackSize -= itemstack2.stackSize;
                par3ItemStack.stackSize -= itemstack3.stackSize;
                return true;
            }

            if (itemstack3 == null && par3ItemStack == null)
            {
                par2ItemStack.stackSize -= itemstack2.stackSize;
                return true;
            }
        }

        return false;
    }
}