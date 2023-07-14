/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.energy;

/**
 * Implementation of {@link IEnergyStorage} that cannot store, receive, or provide energy.
 * Use the {@link #INSTANCE}, don't instantiate. Example:
 * <pre>{@code
 * ItemStack stack = ...;
 * IEnergyStorage storage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(EmptyEnergyStorage.INSTANCE);
 * // Use storage without checking whether it's present.
 * }</pre>
 */
public class EmptyEnergyStorage implements IEnergyStorage
{
    public static final EmptyEnergyStorage INSTANCE = new EmptyEnergyStorage();

    protected EmptyEnergyStorage() {}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored()
    {
        return 0;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return 0;
    }

    @Override
    public boolean canExtract()
    {
        return false;
    }

    @Override
    public boolean canReceive()
    {
        return false;
    }
}
