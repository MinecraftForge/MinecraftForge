package net.minecraft.src;

import java.util.List;

import net.minecraft.src.forge.ISidedInventory;

public class TileEntityBrewingStand extends TileEntity implements IInventory, ISidedInventory
{
    /** The itemstacks currently placed in the slots of the brewing stand */
    private ItemStack[] brewingItemStacks = new ItemStack[4];
    private int brewTime;

    /**
     * an integer with each bit specifying whether that slot of the stand contains a potion
     */
    private int filledSlots;
    private int ingredientID;

    /**
     * Returns the name of the inventory.
     */
    public String getInvName()
    {
        return "container.brewing";
    }

    public int getSizeInventory()
    {
        return this.brewingItemStacks.length;
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity()
    {
        if (this.brewTime > 0)
        {
            --this.brewTime;

            if (this.brewTime == 0)
            {
                this.brewPotions();
                this.onInventoryChanged();
            }
            else if (!this.canBrew())
            {
                this.brewTime = 0;
                this.onInventoryChanged();
            }
            else if (this.ingredientID != this.brewingItemStacks[3].itemID)
            {
                this.brewTime = 0;
                this.onInventoryChanged();
            }
        }
        else if (this.canBrew())
        {
            this.brewTime = 400;
            this.ingredientID = this.brewingItemStacks[3].itemID;
        }

        int var1 = this.getFilledSlots();

        if (var1 != this.filledSlots)
        {
            this.filledSlots = var1;
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, var1);
        }

        super.updateEntity();
    }

    public int getBrewTime()
    {
        return this.brewTime;
    }

    private boolean canBrew()
    {
        if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0)
        {
            ItemStack var1 = this.brewingItemStacks[3];

            if (!Item.itemsList[var1.itemID].isPotionIngredient())
            {
                return false;
            }
            else
            {
                boolean var2 = false;

                for (int var3 = 0; var3 < 3; ++var3)
                {
                    if (this.brewingItemStacks[var3] != null && this.brewingItemStacks[var3].itemID == Item.potion.shiftedIndex)
                    {
                        int var4 = this.brewingItemStacks[var3].getItemDamage();
                        int var5 = this.getPotionResult(var4, var1);

                        if (!ItemPotion.isSplash(var4) && ItemPotion.isSplash(var5))
                        {
                            var2 = true;
                            break;
                        }

                        List var6 = Item.potion.getEffects(var4);
                        List var7 = Item.potion.getEffects(var5);

                        if ((var4 <= 0 || var6 != var7) && (var6 == null || !var6.equals(var7) && var7 != null) && var4 != var5)
                        {
                            var2 = true;
                            break;
                        }
                    }
                }

                return var2;
            }
        }
        else
        {
            return false;
        }
    }

    private void brewPotions()
    {
        if (this.canBrew())
        {
            ItemStack var1 = this.brewingItemStacks[3];

            for (int var2 = 0; var2 < 3; ++var2)
            {
                if (this.brewingItemStacks[var2] != null && this.brewingItemStacks[var2].itemID == Item.potion.shiftedIndex)
                {
                    int var3 = this.brewingItemStacks[var2].getItemDamage();
                    int var4 = this.getPotionResult(var3, var1);
                    List var5 = Item.potion.getEffects(var3);
                    List var6 = Item.potion.getEffects(var4);

                    if ((var3 <= 0 || var5 != var6) && (var5 == null || !var5.equals(var6) && var6 != null))
                    {
                        if (var3 != var4)
                        {
                            this.brewingItemStacks[var2].setItemDamage(var4);
                        }
                    }
                    else if (!ItemPotion.isSplash(var3) && ItemPotion.isSplash(var4))
                    {
                        this.brewingItemStacks[var2].setItemDamage(var4);
                    }
                }
            }

            if (Item.itemsList[var1.itemID].hasContainerItem())
            {
                this.brewingItemStacks[3] = new ItemStack(Item.itemsList[var1.itemID].getContainerItem());
            }
            else
            {
                --this.brewingItemStacks[3].stackSize;

                if (this.brewingItemStacks[3].stackSize <= 0)
                {
                    this.brewingItemStacks[3] = null;
                }
            }
        }
    }

    /**
     * Returns the new potion damage value after the specified item is applied as an ingredient to the specified potion.
     */
    private int getPotionResult(int par1, ItemStack par2ItemStack)
    {
        return par2ItemStack == null ? par1 : (Item.itemsList[par2ItemStack.itemID].isPotionIngredient() ? PotionHelper.applyIngredient(par1, Item.itemsList[par2ItemStack.itemID].getPotionEffect()) : par1);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.brewingItemStacks.length)
            {
                this.brewingItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

        this.brewTime = par1NBTTagCompound.getShort("BrewTime");
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setShort("BrewTime", (short)this.brewTime);
        NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.brewingItemStacks.length; ++var3)
        {
            if (this.brewingItemStacks[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.brewingItemStacks[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return par1 >= 0 && par1 < this.brewingItemStacks.length ? this.brewingItemStacks[par1] : null;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (par1 >= 0 && par1 < this.brewingItemStacks.length)
        {
            ItemStack var3 = this.brewingItemStacks[par1];
            this.brewingItemStacks[par1] = null;
            return var3;
        }
        else
        {
            return null;
        }
    }

    /**
     * works exactly like getStackInSlot, is only used upon closing GUIs
     */
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (par1 >= 0 && par1 < this.brewingItemStacks.length)
        {
            ItemStack var2 = this.brewingItemStacks[par1];
            this.brewingItemStacks[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        if (par1 >= 0 && par1 < this.brewingItemStacks.length)
        {
            this.brewingItemStacks[par1] = par2ItemStack;
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * returns an integer with each bit specifying wether that slot of the stand contains a potion
     */
    public int getFilledSlots()
    {
        int var1 = 0;

        for (int var2 = 0; var2 < 3; ++var2)
        {
            if (this.brewingItemStacks[var2] != null)
            {
                var1 |= 1 << var2;
            }
        }

        return var1;
    }

    @Override
    public int getStartInventorySide(int side) 
    {
        return (side == 1 ? 3 : 0);
    }

    @Override
    public int getSizeInventorySide(int side) 
    {
        return (side == 1 ? 1 : 3);
    }
}
