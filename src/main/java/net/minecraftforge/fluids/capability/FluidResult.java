package net.minecraftforge.fluids.capability;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidResult {
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

    public boolean hasItemStack() {
        return !newStack.isEmpty();
    }

    public ItemStack getItemStack() {
        return newStack;
    }

    public FluidStack getFluidStack() {
        return fluidStack;
    }
}
