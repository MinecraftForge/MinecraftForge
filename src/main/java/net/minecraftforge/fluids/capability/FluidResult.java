package net.minecraftforge.fluids.capability;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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
        return new FluidResult(fluidStack, newStack);
    }

    public static FluidResult of(ItemStack newStack) {
        return new FluidResult(FluidStack.EMPTY, newStack);
    }

    public static FluidResult of(FluidStack fluidStack) {
        return new FluidResult(fluidStack, ItemStack.EMPTY);
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
}
