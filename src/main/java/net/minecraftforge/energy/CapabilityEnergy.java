/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.energy;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
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
            public INBT writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, Direction side)
            {
                return IntNBT.valueOf(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, Direction side, INBT nbt)
            {
                if (!(instance instanceof EnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((EnergyStorage)instance).energy = ((IntNBT)nbt).getInt();
            }
        },
        () -> new EnergyStorage(1000));
    }
}
