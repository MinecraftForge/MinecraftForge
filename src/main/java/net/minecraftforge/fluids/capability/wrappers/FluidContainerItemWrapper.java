package net.minecraftforge.fluids.capability.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * FluidContainerItemWrapper converts an old {@link IFluidContainerItem} to IFluidHandler.
 * Note that successful operations WILL modify the container itemStack.
 * @deprecated will be removed along with {@link IFluidContainerItem}
 */
@Deprecated
public class FluidContainerItemWrapper implements IFluidHandler, ICapabilityProvider
{
    protected final IFluidContainerItem handler;
    protected final ItemStack container;

    public FluidContainerItemWrapper(IFluidContainerItem handler, ItemStack container)
    {
        this.handler = handler;
        this.container = container;
    }

    @Override
    public FluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(handler.getFluid(container), handler.getCapacity(container)) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.stackSize != 1)
        {
            return 0;
        }
        return handler.fill(container, resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.stackSize != 1 || resource == null)
        {
            return null;
        }

        FluidStack canDrain = drain(resource.amount, false);
        if (canDrain != null)
        {
            if (canDrain.isFluidEqual(resource))
            {
                return drain(resource.amount, doDrain);
            }
        }
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.stackSize != 1)
        {
            return null;
        }
        return handler.drain(container, maxDrain, doDrain);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return null;
    }
}
