package net.minecraft.src;

public class ContainerBrewingStand extends Container
{
    private TileEntityBrewingStand tileBrewingStand;
    private int brewTime = 0;

    public ContainerBrewingStand(InventoryPlayer par1InventoryPlayer, TileEntityBrewingStand par2TileEntityBrewingStand)
    {
        this.tileBrewingStand = par2TileEntityBrewingStand;
        this.addSlot(new SlotBrewingStandPotion(this, par1InventoryPlayer.player, par2TileEntityBrewingStand, 0, 56, 46));
        this.addSlot(new SlotBrewingStandPotion(this, par1InventoryPlayer.player, par2TileEntityBrewingStand, 1, 79, 53));
        this.addSlot(new SlotBrewingStandPotion(this, par1InventoryPlayer.player, par2TileEntityBrewingStand, 2, 102, 46));
        this.addSlot(new SlotBrewingStandIngredient(this, par2TileEntityBrewingStand, 3, 79, 17));
        int var3;

        for (var3 = 0; var3 < 3; ++var3)
        {
            for (int var4 = 0; var4 < 9; ++var4)
            {
                this.addSlot(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
            }
        }

        for (var3 = 0; var3 < 9; ++var3)
        {
            this.addSlot(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 142));
        }
    }

    /**
     * Updates crafting matrix; called from onCraftMatrixChanged. Args: none
     */
    public void updateCraftingResults()
    {
        super.updateCraftingResults();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1)
        {
            ICrafting var2 = (ICrafting)this.crafters.get(var1);

            if (this.brewTime != this.tileBrewingStand.getBrewTime())
            {
                var2.updateCraftingInventoryInfo(this, 0, this.tileBrewingStand.getBrewTime());
            }
        }

        this.brewTime = this.tileBrewingStand.getBrewTime();
    }

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            this.tileBrewingStand.setBrewTime(par2);
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.tileBrewingStand.isUseableByPlayer(par1EntityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift clicking.
     */
    public ItemStack transferStackInSlot(int par1)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot)this.inventorySlots.get(par1);

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if ((par1 < 0 || par1 > 2) && par1 != 3)
            {
                if (par1 >= 4 && par1 < 31)
                {
                    if (!this.mergeItemStack(var4, 31, 40, false))
                    {
                        return null;
                    }
                }
                else if (par1 >= 31 && par1 < 40)
                {
                    if (!this.mergeItemStack(var4, 4, 31, false))
                    {
                        return null;
                    }
                }
                else if (!this.mergeItemStack(var4, 4, 40, false))
                {
                    return null;
                }
            }
            else
            {
                if (!this.mergeItemStack(var4, 4, 40, true))
                {
                    return null;
                }

                var3.func_48433_a(var4, var2);
            }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack)null);
            }
            else
            {
                var3.onSlotChanged();
            }

            if (var4.stackSize == var2.stackSize)
            {
                return null;
            }

            var3.onPickupFromSlot(var4);
        }

        return var2;
    }
}
