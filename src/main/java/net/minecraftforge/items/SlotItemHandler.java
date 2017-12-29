package net.minecraftforge.items;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.OptionalInt;

public class SlotItemHandler extends Slot
{
    private final IItemHandler handler;
    private IItemHandler itemHandler;

    public SlotItemHandler(IItemHandler handler, int index, int xPosition, int yPosition)
    {
        super(ItemHandlerHelper.emptyInventory, index, xPosition, yPosition);
        this.handler = handler;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return handler.isStackValidForSlot(stack, getSlotIndex());
    }

    @Override
    @Nonnull
    public ItemStack getStack()
    {
        return handler.getStackInSlot(getSlotIndex());
    }

    @Override
    public void putStack(@Nonnull ItemStack stack)
    {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(getSlotIndex(), stack);
        this.onSlotChanged();

    }

    @Override
    public int getSlotStackLimit()
    {
        return itemHandler.getSlotLimit(getSlotIndex());
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return itemHandler.getStackLimit(stack, getSlotIndex());
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn)
    {
        return false;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        return itemHandler.extract(OptionalInt.of(getSlotIndex()),stack -> true, amount, false);
    }

    @Override
    public boolean isSameInventory(Slot other)
    {
        return other instanceof SlotItemHandler && ((SlotItemHandler) other).getItemHandler() == this.itemHandler;
    }

    public IItemHandler getItemHandler()
    {
        return itemHandler;
    }
}
