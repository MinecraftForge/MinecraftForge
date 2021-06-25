package net.minecraftforge.fluids.capability.context;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;

public interface IFluidActionContext {
    /**
     * Current stack, NEVER MODIFY THIS.
     */
    ItemStack getStack();

    /**
     * Try to replace the current stack, return true if succeeded.
     */
    boolean trySetStack(ItemStack newStack, IFluidHandler.FluidAction action);

    /**
     * Try to insert the stack in the context (without overriding anything), return true if succeeded.
     */
    boolean tryInsertStack(ItemStack newStack, IFluidHandler.FluidAction action);

    /**
     * Get the {@link IFluidHandler} for this stack if it exists.
     */
    default Optional<IFluidHandler> getFluidHandler() {
        return getStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                .map(fluidHandlerItem -> fluidHandlerItem.getFluidHandler(this));
    }

    /**
     * Try to replace one of the current stack by the passed stack.
     * @throws IllegalArgumentException If {@code newStack.getCount()} is not 1.
     */
    default boolean tryReplaceOne(ItemStack newStack, IFluidHandler.FluidAction action) {
        // TODO: allow passing EMPTY to delete one item? (single use fluid container)
        if (newStack.getCount() != 1) {
            throw new IllegalArgumentException();
        }

        // Try to replace empty
        if (getStack().isEmpty() && trySetStack(newStack, action)) {
            return true;
        }
        // Otherwise try to add to stack
        if (ItemHandlerHelper.canItemStacksStack(getStack(), newStack) && getStack().getCount() < getStack().getMaxStackSize()) {
            newStack.setCount(getStack().getCount()+1);
            if (trySetStack(newStack, action)) {
                return true;
            }
            // If it fails go back to a count of 1
            newStack.setCount(1);
        }
        // Last resort: try to insert wherever
        return tryInsertStack(newStack, action);
    }
}
