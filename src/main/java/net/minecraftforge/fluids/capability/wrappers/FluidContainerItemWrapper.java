/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fluids.capability.wrappers;

import javax.annotation.Nullable;

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
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return null;
    }
}
