package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Wraps a liquid container that uses the {@link FluidContainerRegistry}
 * Note that successful operations WILL modify the container itemStack.
 * @deprecated will be removed along with {@link FluidContainerRegistry}
 */
@Deprecated
public class FluidContainerRegistryWrapper implements IFluidHandler, ICapabilityProvider
{
    protected final ItemStack container;

    public FluidContainerRegistryWrapper(ItemStack container)
    {
        this.container = container;
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(container);
        int capacity = FluidContainerRegistry.getContainerCapacity(fluid, container);
        return new FluidTankProperties[] { new FluidTankProperties(fluid, capacity) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.stackSize != 1 || resource == null)
        {
            return 0;
        }

        FluidStack originalContained = FluidContainerRegistry.getFluidForFilledItem(container);

        ItemStack result = FluidContainerRegistry.fillFluidContainer(resource, container);
        if (result == null)
        {
            return 0;
        }

        if (doFill)
        {
            container.deserializeNBT(result.serializeNBT());
        }

        FluidStack newContained = FluidContainerRegistry.getFluidForFilledItem(result);

        int originalAmount = originalContained == null ? 0 : originalContained.amount;
        int newAmount = newContained == null ? 0 : newContained.amount;
        return newAmount - originalAmount;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.stackSize != 1 || resource == null)
        {
            return null;
        }

        FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(container);
        if (contained != null && contained.isFluidEqual(resource))
        {
            return drain(resource.amount, doDrain);
        }

        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.stackSize != 1)
        {
            return null;
        }

        FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(container);
        if (contained != null)
        {
            if (contained.amount <= maxDrain)
            {
                ItemStack emptyContainer = FluidContainerRegistry.drainFluidContainer(container);
                if (emptyContainer != null)
                {
                    if (doDrain)
                    {
                        if (FluidContainerRegistry.hasNullEmptyContainer(container))
                        {
                            emptyContainer.stackSize = 0;
                        }
                        container.deserializeNBT(emptyContainer.serializeNBT());
                    }
                    return contained;
                }
            }
        }

        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return null;
    }
}
