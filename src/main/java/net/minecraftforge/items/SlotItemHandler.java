package net.minecraftforge.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotItemHandler extends Slot
{
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    public final IItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack == null)
            return false;
        ItemStack remainder = this.itemHandler.insertItem(index, stack, true);
        return remainder == null || remainder.stackSize < stack.stackSize;
    }

    @Override
    public ItemStack getStack()
    {
        return this.itemHandler.getStackInSlot(index);
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void putStack(ItemStack stack)
    {
        ((IItemHandlerModifiable) this.itemHandler).setStackInSlot(index, stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_)
    {

    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        maxAdd.stackSize = maxAdd.getMaxStackSize();
        ItemStack currentStack = this.itemHandler.getStackInSlot(index);
        ItemStack remainder = this.itemHandler.insertItem(index, maxAdd, true);

        int current = currentStack == null ? 0 : currentStack.stackSize;
        int added = maxAdd.stackSize - (remainder != null ? remainder.stackSize : 0);
        return current + added;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return this.itemHandler.extractItem(index, 1, true) != null;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        return this.itemHandler.extractItem(index, amount, false);
    }
}