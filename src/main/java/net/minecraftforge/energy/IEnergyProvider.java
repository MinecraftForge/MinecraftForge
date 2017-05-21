package net.minecraftforge.energy;

import net.minecraft.util.EnumFacing;

public interface IEnergyProvider extends IEnergyBase 
{

    /**
    * Checks to see if this has any energy. Checks to see if energy can be extracted from that side
    *
    * @param side
    *           Side from which energy is to be extracted
    * If FALSE extractEnergy returns 0
     */
    public boolean canExtract(EnumFacing side);

    /**
    * Removes energy from the storage. Returns quantity of energy that was removed.
    *
    * @param maxExtract
    *               Maximum amount of energy to be extracted
    * @param side
    *               Side from which energy is to be extracted
    * @param simulate
    *               If TRUE do the calculations and return the extracted value but do not subtract that energy from the stored amount
    * @return Amount of energy that was extracted or simulated
     */
    public int extractEnergy(int maxExtract, EnumFacing side, boolean simulate);

    /**
    * Sets the maximum amount of energy that can be extracted
     */
    public void setMaxExtract(int value);

    /**
    * Returns the maximum amount of energy that can be extracted
     */
    public int getMaxExtract();

}
