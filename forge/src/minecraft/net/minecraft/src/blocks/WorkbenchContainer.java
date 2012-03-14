package net.minecraft.src.blocks;

import java.util.List;
import net.minecraft.src.*;

public class WorkbenchContainer extends Container
{
    public InventoryCrafting craftMatrix;
    public IInventory craftResult;
    private World worldObj;

    public WorkbenchContainer(InventoryPlayer inventoryplayer, World world)
    {
        craftMatrix = new InventoryCrafting(this, 3, 3);
        craftResult = new InventoryCraftResult();
        worldObj = world;
        addSlot(new SlotCrafting(inventoryplayer.player, craftMatrix, craftResult, 0, 124, 35));
        for (int i = 0; i < 3; i++)
        {
            for (int l = 0; l < 3; l++)
            {
                addSlot(new Slot(craftMatrix, l + i * 3, 30 + l * 18, 17 + i * 18));
            }
        }

        for (int j = 0; j < 3; j++)
        {
            for (int i1 = 0; i1 < 9; i1++)
            {
                addSlot(new Slot(inventoryplayer, i1 + j * 9 + 9, 8 + i1 * 18, 84 + j * 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(inventoryplayer, k, 8 + k * 18, 142));
        }

        onCraftMatrixChanged(craftMatrix);
    }

    public void onCraftMatrixChanged(IInventory iinventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix));
    }

    public void onCraftGuiClosed(EntityPlayer entityplayer)
    {
        super.onCraftGuiClosed(entityplayer);
        if (worldObj.isRemote)
        {
            return;
        }
        for (int i = 0; i < 9; i++)
        {
            ItemStack itemstack = craftMatrix.getStackInSlot(i);
            if (itemstack != null)
            {
                entityplayer.func_48153_a(itemstack);
            }
        }
    }

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

    public ItemStack transferStackInSlot(int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i == 0)
            {
                if (!mergeItemStack(itemstack1, 10, 46, true))
                {
                    return null;
                }
            }
            else if (i >= 10 && i < 37)
            {
                if (!mergeItemStack(itemstack1, 37, 46, false))
                {
                    return null;
                }
            }
            else if (i >= 37 && i < 46)
            {
                if (!mergeItemStack(itemstack1, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(itemstack1, 10, 46, false))
            {
                return null;
            }
            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(itemstack1);
            }
            else
            {
                return null;
            }
        }
        return itemstack;
    }
}
