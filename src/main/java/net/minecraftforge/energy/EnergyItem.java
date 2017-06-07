/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraftforge.energy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Reference implementation of {@link IEnergyItem}. Use/extend this or implement your own.
 *
 * Derived from {@link EnergyStorage}. For easy handling of energy items, it is recommended to use the {@link EnergyItems}
 * class when handling charging or discharging of energy items.
 *
 * @see IEnergyItem
 * @author ExE Boss
 */
public class EnergyItem extends Item implements IEnergyItem
{
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public EnergyItem(int capacity)
    {
        this(capacity, capacity, capacity);
    }

    public EnergyItem(int capacity, int maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer);
    }

    public EnergyItem(int capacity, int maxReceive, int maxExtract)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveEnergy(ItemStack itemStack, int maxReceive, boolean simulate)
    {
        if (!canReceive())
            return 0;

        if (!(itemStack.getItem() instanceof EnergyItem))
            return 0;

        NBTTagCompound nbt = getAndInitializeTagCompound(itemStack);
        int energy = nbt.getInteger("energy");

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            energy += energyReceived;

        nbt.setInteger("energy", energy);
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack itemStack, int maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        if (!(itemStack.getItem() instanceof EnergyItem))
            return 0;

        NBTTagCompound nbt = getAndInitializeTagCompound(itemStack);
        int energy = nbt.getInteger("energy");

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            energy += energyReceived;

        nbt.setInteger("energy", energy);
        return energyReceived;
    }

    @Override
    public int getEnergyStored(ItemStack itemStack)
    {
        if (!(itemStack.getItem() instanceof EnergyItem))
            return 0;

        NBTTagCompound nbt = getAndInitializeTagCompound(itemStack);
        return nbt.getInteger("energy");
    }

    /**
    * Returns the maximum amount of energy that can be stored in the item.
    */
    public int getMaxEnergyStored()
    {
        return this.capacity;
    }

    /**
    * {@inheritDoc}
    * <p>
    * The default implementation delegates calls to {@link #getMaxEnergyStored()}.
    */
    @Override
    public int getMaxEnergyStored(ItemStack itemStack) {
        return getMaxEnergyStored();
    }

    /**
    * Returns if this item can have energy extracted.
    * If this is false, then any calls to extractEnergy will return 0.
    */
    public boolean canExtract()
    {
        return this.maxExtract > 0;
    }

    /**
    * {@inheritDoc}
    * <p>
    * The default implementation delegates calls to {@link #canExtract()}.
    */
    @Override
    public boolean canExtract(ItemStack itemStack) {
        return canExtract();
    }

    /**
    * Used to determine if this item can receive energy.
    * If this is false, then any calls to receiveEnergy will return 0.
    */
    public boolean canReceive()
    {
        return this.maxReceive > 0;
    }

    /**
    * {@inheritDoc}
    * <p>
    * The default implementation delegates calls to {@link #canReceive()}.
    */
    @Override
    public boolean canReceive(ItemStack itemStack) {
        return canReceive();
    }

    /**
    * If the Item contained in the ItemStack is an instance of EnergyItem, then ensure that the ItemStack
    * has an NBTTagCompound and that the NBTTagCompound has an NBTTagInteger with the name "energy".
    *
    * @param itemStack
    *            The ItemStack instance.
    * @return The NBTTagCompound of the ItemStack. Can be null if the Item contained in the ItemStack is not an instance of EnergyItem
    */
    protected static final NBTTagCompound getAndInitializeTagCompound(ItemStack itemStack)
    {
        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt == null)
        {
            nbt = new NBTTagCompound();
            itemStack.setTagCompound(nbt);
        }

        if (!nbt.hasKey("energy") && itemStack.getItem() instanceof EnergyItem)
            nbt.setInteger("energy", 0);

        return nbt;
    }
}
