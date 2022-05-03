/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fluids.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CapabilityFluidHandler
{
    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;
    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IFluidHandler.class, new DefaultFluidHandlerStorage<>(), () -> new FluidTank(FluidAttributes.BUCKET_VOLUME));

        CapabilityManager.INSTANCE.register(IFluidHandlerItem.class, new DefaultFluidHandlerStorage<>(), () -> new FluidHandlerItemStack(new ItemStack(Items.BUCKET), FluidAttributes.BUCKET_VOLUME));
    }

    private static class DefaultFluidHandlerStorage<T extends IFluidHandler> implements Capability.IStorage<T> {
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side)
        {
            if (!(instance instanceof FluidTank))
                throw new RuntimeException("Cannot serialize to an instance that isn't the default implementation");
            CompoundNBT nbt = new CompoundNBT();
            FluidTank tank = (FluidTank) instance;
            FluidStack fluid = tank.getFluid();
            fluid.writeToNBT(nbt);
            nbt.putInt("Capacity", tank.getCapacity());
            return nbt;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt)
        {
            if (!(instance instanceof FluidTank))
                throw new RuntimeException("Cannot deserialize to an instance that isn't the default implementation");
            CompoundNBT tags = (CompoundNBT) nbt;
            FluidTank tank = (FluidTank) instance;
            tank.setCapacity(tags.getInt("Capacity"));
            tank.readFromNBT(tags);
        }
    }
}
