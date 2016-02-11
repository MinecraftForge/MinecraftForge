package net.minecraftforge.fluids.capability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidTankItemStack;

public class ItemFluidContainer extends Item
{
    protected int capacity;

    public ItemFluidContainer(int capacity)
    {
        super();
        this.capacity = capacity;
    }

    public ItemFluidContainer setCapacity(int capacity)
    {
        this.capacity = capacity;
        return this;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new FluidTankItemStack(stack, capacity);
    }
}
