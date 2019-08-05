/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraftforge.common.capabilities.accessor.IFlowCapabilityAccessor;

/**
 * Reference implementation of {@link IEnergyStorage}. Use/extend this or implement your own.
 *
 * Derived from the Redstone Flux power system designed by King Lemming and originally utilized in Thermal Expansion and related mods.
 * Created with consent and permission of King Lemming and Team CoFH. Released with permission under LGPL 2.1 when bundled with Forge.
 */
public class EnergyStorage implements IEnergyStorage
{
    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyStorage(int capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(int capacity, int maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract, int energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            setEnergyStored(energy + energyReceived);

        return energyReceived;
    }

    @Override
    public int receiveEnergy(int maxReceive, IFlowCapabilityAccessor accessor)
    {
        //Enforce take all, not enough storage space
        if(accessor.requireFull() && (getEnergyStored() + maxReceive > getMaxEnergyStored())) {
            return 0;
        }
        //Bypass
        else if(accessor.bypassLimits())
        {
            int energyReceived = Math.min(capacity - energy, maxReceive);
            if (!accessor.simulate())
                setEnergyStored(energy + energyReceived);

            return energyReceived;
        }
        //Enforce take all, breaks max limit check
        else if(accessor.requireFull() && maxReceive > this.maxReceive) {
            return 0;
        }
        return receiveEnergy(maxReceive, accessor.simulate());
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            setEnergyStored(energy - energyExtracted);

        return energyExtracted;
    }

    @Override
    public int extractEnergy(int maxExtract, IFlowCapabilityAccessor accessor)
    {
        //Enforce remove all, don't have enough energy check
        if(accessor.requireFull() && maxExtract > energy) {
            return 0;
        }
        //Bypass
        else if(accessor.bypassLimits())
        {
            int energyExtracted = Math.min(energy, maxExtract);
            if (!accessor.simulate())
                setEnergyStored(energy - energyExtracted);

            return energyExtracted;
        }
        //Enforce remove all, breaks max limit check
        else if(accessor.requireFull() && (maxExtract > this.maxExtract)) {
            return 0;
        }
        return extractEnergy(maxExtract, accessor.simulate());
    }

    /**
     * Triggered any time the energy state changes.
     *
     * @param prevValue - value before changes were applied
     * @param newValue - new value after changes were applied
     */
    protected void onEnergyChanged(int prevValue, int newValue)
    {
        //Override this to implement update/sync logic
    }

    @Override
    public int getEnergyStored()
    {
        return energy;
    }

    /**
     * Allows setting the energy directly
     *
     * @param value - value to set
     */
    public void setEnergyStored(int value)
    {
        final int prevEnergy = this.energy;
        this.energy = Math.max(0, Math.min(value, getMaxEnergyStored()));
        if (prevEnergy != this.energy)
        {
            onEnergyChanged(prevEnergy, value);
        }
    }

    @Override
    public int getMaxEnergyStored()
    {
        return capacity;
    }

    @Override
    public boolean canExtract()
    {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive()
    {
        return this.maxReceive > 0;
    }
}
