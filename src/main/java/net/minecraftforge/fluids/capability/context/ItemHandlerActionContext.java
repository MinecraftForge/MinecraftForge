package net.minecraftforge.fluids.capability.context;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemHandlerActionContext implements IFluidActionContext {
    private final IItemHandlerModifiable handler;
    private final int slot;

    public ItemHandlerActionContext(IItemHandlerModifiable handler, int slot) {
        this.handler = handler;
        this.slot = slot;
    }

    @Override
    public ItemStack getStack() {
        return handler.getStackInSlot(slot);
    }

    @Override
    public boolean trySetStack(ItemStack newStack, IFluidHandler.FluidAction action) {
        if (action.execute()) {
            handler.setStackInSlot(slot, newStack);
        }
        return true;
    }

    @Override
    public boolean tryInsertStack(ItemStack newStack, IFluidHandler.FluidAction action) {
        return ItemHandlerHelper.insertItemStacked(handler, newStack, action.simulate()).isEmpty();
    }
}
