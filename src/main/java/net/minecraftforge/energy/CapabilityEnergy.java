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

package net.minecraftforge.energy;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityEnergy
{
    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IEnergyStorage.class, new IStorage<IEnergyStorage>()
        {
            @Override
            public NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side)
            {
                return new NBTTagInt(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side, NBTBase nbt)
            {
                if (!(instance instanceof EnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((EnergyStorage)instance).energy = ((NBTTagInt)nbt).getInt();
            }
        },
        () -> new EnergyStorage(1000));
    }
}
