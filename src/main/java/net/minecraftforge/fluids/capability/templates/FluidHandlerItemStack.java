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

package net.minecraftforge.fluids.capability.templates;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

/**
 * FluidHandlerItemStack is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old deprecated {@link ItemFluidContainer}.
 *
 * This class allows an itemStack to contain any partial level of fluid up to its capacity, unlike {@link FluidHandlerItemStackSimple}
 *
 * Additional examples are provided to enable consumable fluid containers (see {@link Consumable}),
 * fluid containers with different empty and full items (see {@link SwapEmpty},
 */
public class FluidHandlerItemStack implements IFluidHandler, ICapabilityProvider
{
    public static final String FLUID_NBT_KEY = "Fluid";

    protected final ItemStack container;
    protected final int capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public FluidHandlerItemStack(ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    @Nullable
    public FluidStack getFluid()
    {
        NBTTagCompound tagCompound = container.getTagCompound();
        if (tagCompound == null || !tagCompound.hasKey(FLUID_NBT_KEY))
        {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FLUID_NBT_KEY));
    }

    protected void setFluid(FluidStack fluid)
    {
        if (!container.hasTagCompound())
        {
            container.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound fluidTag = new NBTTagCompound();
        fluid.writeToNBT(fluidTag);
        container.getTagCompound().setTag(FLUID_NBT_KEY, fluidTag);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), capacity) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !canFillFluidType(resource))
        {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained == null)
        {
            int fillAmount = Math.min(capacity, resource.amount);

            if (doFill)
            {
                FluidStack filled = resource.copy();
                filled.amount = fillAmount;
                setFluid(filled);
            }

            return fillAmount;
        }
        else
        {
            if (contained.isFluidEqual(resource))
            {
                int fillAmount = Math.min(capacity - contained.amount, resource.amount);

                if (doFill && fillAmount > 0) {
                    contained.amount += fillAmount;
                    setFluid(contained);
                }

                return fillAmount;
            }

            return 0;
        }
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !resource.isFluidEqual(getFluid()))
        {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.stackSize != 1 || maxDrain <= 0)
        {
            return null;
        }

        FluidStack contained = getFluid();
        if (contained == null || contained.amount <= 0 || !canDrainFluidType(contained))
        {
            return null;
        }

        final int drainAmount = Math.min(contained.amount, maxDrain);

        FluidStack drained = contained.copy();
        drained.amount = drainAmount;

        if (doDrain)
        {
            contained.amount -= drainAmount;
            if (contained.amount == 0)
            {
                setContainerToEmpty();
            }
            else
            {
                setFluid(contained);
            }
        }

        return drained;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid)
    {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out the container's item for a different one with "container.setItem".
     * Can be used to destroy the container with "container.stackSize--"
     */
    protected void setContainerToEmpty()
    {
        container.getTagCompound().removeTag(FLUID_NBT_KEY);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
    }

    /**
     * Destroys the container item when it's emptied.
     */
    public static class Consumable extends FluidHandlerItemStack
    {
        public Consumable(ItemStack container, int capacity)
        {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.stackSize--;
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStack
    {
        protected final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity)
        {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty()
        {
            super.setContainerToEmpty();
            container.deserializeNBT(emptyContainer.serializeNBT());
        }
    }
}
