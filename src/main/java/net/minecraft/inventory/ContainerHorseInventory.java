package net.minecraft.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container
{
    private IInventory field_111243_a;
    private EntityHorse theHorse;
    private static final String __OBFID = "CL_00001751";

    public ContainerHorseInventory(IInventory par1IInventory, final IInventory par2IInventory, final EntityHorse par3EntityHorse)
    {
        this.field_111243_a = par2IInventory;
        this.theHorse = par3EntityHorse;
        byte b0 = 3;
        par2IInventory.openChest();
        int i = (b0 - 4) * 18;
        this.addSlotToContainer(new Slot(par2IInventory, 0, 8, 18)
        {
            private static final String __OBFID = "CL_00001752";
            // JAVADOC METHOD $$ func_75214_a
            public boolean isItemValid(ItemStack par1ItemStack)
            {
                return super.isItemValid(par1ItemStack) && par1ItemStack.getItem() == Items.saddle && !this.getHasStack();
            }
        });
        this.addSlotToContainer(new Slot(par2IInventory, 1, 8, 36)
        {
            private static final String __OBFID = "CL_00001753";
            // JAVADOC METHOD $$ func_75214_a
            public boolean isItemValid(ItemStack par1ItemStack)
            {
                return super.isItemValid(par1ItemStack) && par3EntityHorse.func_110259_cr() && EntityHorse.func_146085_a(par1ItemStack.getItem());
            }
            @SideOnly(Side.CLIENT)
            public boolean func_111238_b()
            {
                return par3EntityHorse.func_110259_cr();
            }
        });
        int j;
        int k;

        if (par3EntityHorse.isChested())
        {
            for (j = 0; j < b0; ++j)
            {
                for (k = 0; k < 5; ++k)
                {
                    this.addSlotToContainer(new Slot(par2IInventory, 2 + k + j * 5, 80 + k * 18, 18 + j * 18));
                }
            }
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(par1IInventory, k + j * 9 + 9, 8 + k * 18, 102 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(par1IInventory, j, 8 + j * 18, 160 + i));
        }
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.field_111243_a.isUseableByPlayer(par1EntityPlayer) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(par1EntityPlayer) < 8.0F;
    }

    // JAVADOC METHOD $$ func_82846_b
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 < this.field_111243_a.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.field_111243_a.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, false))
                {
                    return null;
                }
            }
            else if (this.getSlot(0).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return null;
                }
            }
            else if (this.field_111243_a.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.field_111243_a.getSizeInventory(), false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    // JAVADOC METHOD $$ func_75134_a
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
        this.field_111243_a.closeChest();
    }
}