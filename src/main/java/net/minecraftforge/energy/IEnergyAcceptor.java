package net.minecraftforge.energy;

import net.minecraft.util.EnumFacing;

public interface IEnergyAcceptor extends IEnergyBase {

    /**
    * Checks to see if this is full. Checks to see if energy can be received from that side
    *
    * @param side
    *           Side from which energy is to be received
    * If FALSE receiveEnergy returns 0
     */
    public boolean canReceive(EnumFacing side);

    /**
    * Add energy to the storage. Returns amount of energy that was added.
    *
    * @param maxReceive
    *               Maximum amount of energy to be received
    * @param side
    *               Side from which energy is to be received
    * @param simulate
    *               If TRUE do the calculations and return the received value but do not add that energy to the stored amount
    * @return Amount of energy that was received or simulated
     */
    public int receiveEnergy(int maxReceive, EnumFacing side, boolean simulate);

    /**
    * Sets the maximum amount of energy that can be received
     */
    public void setMaxReceive(int value);

    /**
    * Returns the maximum amount of energy that can be received
     */
    public int getMaxReceive();

}
