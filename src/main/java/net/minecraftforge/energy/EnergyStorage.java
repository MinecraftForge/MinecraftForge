/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Reference implementation of {@link IEnergyStorage}. Use/extend this or implement your own.
 *
 * Derived from the Redstone Flux power system designed by King Lemming and originally utilized in Thermal Expansion and related mods.
 * Created with consent and permission of King Lemming and Team CoFH. Released with permission under LGPL 2.1 when bundled with Forge.
 */
public class EnergyStorage implements IEnergyStorage, INBTSerializable<Tag>
{
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;

    public EnergyStorage(long capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public EnergyStorage(long capacity, long maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorage(long capacity, long maxReceive, long maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorage(long capacity, long maxReceive, long maxExtract, long energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate)
    {
        if (!canReceive())
            return 0;

        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            energy += energyReceived;
        return energyReceived;
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            energy -= energyExtracted;
        return energyExtracted;
    }

    @Override
    public long getEnergyStored()
    {
        return energy;
    }

    @Override
    public long getMaxEnergyStored()
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

    @Override
    public Tag serializeNBT()
    {
        return LongTag.valueOf(this.getEnergyStored());
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
        if (!(nbt instanceof LongTag longNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.energy = longNbt.getAsLong();
    }
}
