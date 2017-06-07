/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.minecraftforge.energy;

import net.minecraft.item.ItemStack;

/**
 * An energy storage is the unit of interaction with Energy inventories.
 * <p>
 * A reference implementation can be found at {@link EnergyItem}.
 *
 * Derived from the {@link IEnergyStorage} class, but with Items in mind.
 *
 * @see EnergyItem
 * @see IEnergyStorage
 * @author ExE Boss
 */
public interface IEnergyItem
{
    /**
    * Adds energy to the item. Returns quantity of energy that was accepted.
    *
    * @param itemStack
    *            The ItemStack that contains the item.
    * @param maxReceive
    *            Maximum amount of energy to be inserted.
    * @param simulate
    *            If TRUE, the insertion will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
    */
    int receiveEnergy(ItemStack itemStack, int maxReceive, boolean simulate);

    /**
    * Removes energy from the item. Returns quantity of energy that was removed.
    *
    * @param itemStack
    *            The ItemStack that contains the item.
    * @param maxExtract
    *            Maximum amount of energy to be extracted.
    * @param simulate
    *            If TRUE, the extraction will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
    */
    int extractEnergy(ItemStack itemStack, int maxExtract, boolean simulate);

    /**
    * Returns the amount of energy currently stored in the item.
    *
    * @param itemStack
    *            The ItemStack that contains the item.
    */
    int getEnergyStored(ItemStack itemStack);

    /**
    * Returns the maximum amount of energy that can be stored in the item.
    *
    * @param itemStack
    *            The ItemStack that contains the item.
    */
    int getMaxEnergyStored(ItemStack itemStack);

    /**
    * Returns if this item can have energy extracted.
    * If this is false, then any calls to extractEnergy will return 0.
    *
    * @param itemStack
    *            The ItemStack that contains the item.
    */
    boolean canExtract(ItemStack itemStack);

    /**
    * Used to determine if this item can receive energy.
    * If this is false, then any calls to receiveEnergy will return 0.
    *
    * @param itemStack
    *            The ItemStack that contains the item.
    */
    boolean canReceive(ItemStack itemStack);

}
