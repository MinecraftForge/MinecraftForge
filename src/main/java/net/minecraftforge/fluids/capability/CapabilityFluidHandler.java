/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fluids.capability;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class CapabilityFluidHandler
{
    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;
    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IFluidHandler.class, new DefaultFluidHandlerStorage<>(), () -> new FluidTank(Fluid.BUCKET_VOLUME));

        CapabilityManager.INSTANCE.register(IFluidHandlerItem.class, new DefaultFluidHandlerStorage<>(), () -> new FluidHandlerItemStack(new ItemStack(Items.BUCKET), Fluid.BUCKET_VOLUME));
    }

    private static class DefaultFluidHandlerStorage<T extends IFluidHandler> implements Capability.IStorage<T> {
        @Override
		public INBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side)
		{
			if (!(instance instanceof IFluidTank))
				throw new RuntimeException("IFluidHandler instance does not implement IFluidTank");
			NBTTagCompound nbt = new NBTTagCompound();
			IFluidTank tank = (IFluidTank) instance;
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
			{
				fluid.writeToNBT(nbt);
			}
			else
			{
				nbt.setString("Empty", "");
			}
			nbt.setInteger("Capacity", tank.getCapacity());
			return nbt;
		}

        @Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, INBTBase nbt)
		{
			if (!(instance instanceof FluidTank))
				throw new RuntimeException("IFluidHandler instance is not instance of FluidTank");
			NBTTagCompound tags = (NBTTagCompound) nbt;
			FluidTank tank = (FluidTank) instance;
			tank.setCapacity(tags.getInteger("Capacity"));
			tank.readFromNBT(tags);
		}
    }
}
