package net.minecraftforge.fmp.capabilities;

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CapabilityWrapperItemHandler implements ICapabilityWrapper<IItemHandler>
{

    @Override
    public IItemHandler wrapImplementations(Collection<IItemHandler> implementations)
    {
        for (IItemHandler handler : implementations)
        {
            if (handler instanceof IItemHandlerModifiable)
            {
                return new WrappedCapabilityItemHandlerModifiable(implementations);
            }
        }

        return new WrappedCapabilityItemHandler(implementations);
    }

    private static class WrappedCapabilityItemHandler implements IItemHandler
    {
        protected final Collection<IItemHandler> implementations;

        public WrappedCapabilityItemHandler(Collection<IItemHandler> implementations)
        {
            this.implementations = implementations;
        }

        @Override
        public int getSlots()
        {
            int total = 0;
            for (IItemHandler handler : implementations)
            {
                total += handler.getSlots();
            }
            return total;
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            int total = 0;
            for (IItemHandler handler : implementations)
            {
                int slots = handler.getSlots();
                if (slot < total + slots)
                {
                    return handler.getStackInSlot(slot - total);
                }
                total += slots;
            }
            return null;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            int total = 0;
            for (IItemHandler handler : implementations)
            {
                int slots = handler.getSlots();
                if (slot < total + slots)
                {
                    return handler.insertItem(slot - total, stack, simulate);
                }
                total += slots;
            }
            return null;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            int total = 0;
            for (IItemHandler handler : implementations)
            {
                int slots = handler.getSlots();
                if (slot < total + slots)
                {
                    return handler.extractItem(slot - total, amount, simulate);
                }
                total += slots;
            }
            return null;
        }

    }

    private static class WrappedCapabilityItemHandlerModifiable extends WrappedCapabilityItemHandler implements IItemHandlerModifiable
    {

        public WrappedCapabilityItemHandlerModifiable(Collection<IItemHandler> implementations)
        {
            super(implementations);
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack)
        {
            int total = 0;
            for (IItemHandler handler : implementations)
            {
                int slots = handler.getSlots();
                if (slot < total + slots)
                {
                    if (handler instanceof IItemHandlerModifiable)
                    {
                        ((IItemHandlerModifiable) handler).setStackInSlot(slot - total, stack);
                    }
                    else
                    {
                        return;
                    }
                }
                total += slots;
            }
        }

    }

}
