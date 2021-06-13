package net.minecraftforge.fluids.capability;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Combines FluidStack and ItemStack along with some utility functions. This is primarily used for
 * Fluid Handling for items.
 */
public class FluidResult {
    // FluidResult.EMPTY means that the operation did not successfully happen.
    public static FluidResult EMPTY = FluidResult.of(FluidStack.EMPTY, ItemStack.EMPTY);

    FluidStack fluidStack;
    ItemStack newStack;

    private FluidResult(FluidStack fluidStack, ItemStack newStack) {
        this.fluidStack = fluidStack;
        this.newStack = newStack;
    }

    public static FluidResult of(FluidStack fluidStack, ItemStack newStack) {
        return new FluidResult(fluidStack.copy(), newStack.copy());
    }

    public static FluidResult of(ItemStack newStack) {
        return new FluidResult(FluidStack.EMPTY, newStack.copy());
    }

    public static FluidResult of(FluidStack fluidStack) {
        return new FluidResult(fluidStack.copy(), ItemStack.EMPTY);
    }

    public boolean hasItemStack() {
        return !newStack.isEmpty();
    }

    public ItemStack getItemStack() {
        return newStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }

    public void setItemStack(ItemStack stack) {
        newStack = stack;
    }

    public void setFluidStack(FluidStack stack) {
        fluidStack = stack;
    }

    public boolean isEmpty() {
        return FluidResult.EMPTY == this;
    }

    public FluidResult copy() {
        return FluidResult.of(getFluidStack(), getItemStack());
    }

    public int getFluidAmount() {
        return getFluidStack().getAmount();
    }

    public void setFluidAmount(int amount) {
        getFluidStack().setAmount(amount);
    }

    /**
     * Checks if fluidstack in FluidResult is equal to fluidstack
     * @param other FluidStack to compare to
     * @return boolean that states whether fluidstacks are equal.
     */
    public boolean isFluidEqual(@Nonnull FluidStack other)
    {
        return other.isFluidEqual(getFluidStack());
    }

    public Fluid getFluid()
    {
        return getFluidStack().getFluid();
    }
}
