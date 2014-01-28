package net.minecraft.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryBasic implements IInventory
{
    private String inventoryTitle;
    private int slotsCount;
    private ItemStack[] inventoryContents;
    private List field_70480_d;
    private boolean field_94051_e;
    private static final String __OBFID = "CL_00001514";

    public InventoryBasic(String par1Str, boolean par2, int par3)
    {
        this.inventoryTitle = par1Str;
        this.field_94051_e = par2;
        this.slotsCount = par3;
        this.inventoryContents = new ItemStack[par3];
    }

    public void func_110134_a(IInvBasic par1IInvBasic)
    {
        if (this.field_70480_d == null)
        {
            this.field_70480_d = new ArrayList();
        }

        this.field_70480_d.add(par1IInvBasic);
    }

    public void func_110132_b(IInvBasic par1IInvBasic)
    {
        this.field_70480_d.remove(par1IInvBasic);
    }

    // JAVADOC METHOD $$ func_70301_a
    public ItemStack getStackInSlot(int par1)
    {
        return this.inventoryContents[par1];
    }

    // JAVADOC METHOD $$ func_70298_a
    public ItemStack decrStackSize(int par1, int par2)
    {
        if (this.inventoryContents[par1] != null)
        {
            ItemStack itemstack;

            if (this.inventoryContents[par1].stackSize <= par2)
            {
                itemstack = this.inventoryContents[par1];
                this.inventoryContents[par1] = null;
                this.onInventoryChanged();
                return itemstack;
            }
            else
            {
                itemstack = this.inventoryContents[par1].splitStack(par2);

                if (this.inventoryContents[par1].stackSize == 0)
                {
                    this.inventoryContents[par1] = null;
                }

                this.onInventoryChanged();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70304_b
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.inventoryContents[par1] != null)
        {
            ItemStack itemstack = this.inventoryContents[par1];
            this.inventoryContents[par1] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_70299_a
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.inventoryContents[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    // JAVADOC METHOD $$ func_70302_i_
    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    public String func_145825_b()
    {
        return this.inventoryTitle;
    }

    public boolean func_145818_k_()
    {
        return this.field_94051_e;
    }

    public void func_110133_a(String par1Str)
    {
        this.field_94051_e = true;
        this.inventoryTitle = par1Str;
    }

    // JAVADOC METHOD $$ func_70297_j_
    public int getInventoryStackLimit()
    {
        return 64;
    }

    // JAVADOC METHOD $$ func_70296_d
    public void onInventoryChanged()
    {
        if (this.field_70480_d != null)
        {
            for (int i = 0; i < this.field_70480_d.size(); ++i)
            {
                ((IInvBasic)this.field_70480_d.get(i)).onInventoryChanged(this);
            }
        }
    }

    // JAVADOC METHOD $$ func_70300_a
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    public void openChest() {}

    public void closeChest() {}

    // JAVADOC METHOD $$ func_94041_b
    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
    {
        return true;
    }
}