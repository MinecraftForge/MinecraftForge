package net.minecraftforge.items.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

// combines multiple IItemHandlerModifiable into one interface
public class CombinedInvWrapper implements IItemHandlerModifiable
{

    protected final IItemHandlerModifiable[] itemHandler; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // numeber of total slots

    public CombinedInvWrapper(IItemHandlerModifiable... itemHandler)
    {
        this.itemHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++)
        {
            index += itemHandler[i].getSlots();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= itemHandler.length)
        {
            return (IItemHandlerModifiable)EmptyHandler.INSTANCE;
        }
        return itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack)
    {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots()
    {
        return slotCount;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }
}
