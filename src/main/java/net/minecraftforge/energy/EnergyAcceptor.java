package net.minecraftforge.energy;

import net.minecraft.util.EnumFacing;

/**
* Example implementation of an energy acceptor such as a machine
 */
public class EnergyAcceptor implements IEnergyAcceptor {

    protected int maxEnergy;
    protected int energy;
    protected int maxReceive;
    protected EnumFacing facing;

    public EnergyAcceptor(int maxEnergy, int energy, int maxReceive, EnumFacing facing){
        this.maxEnergy = maxEnergy;
        this.energy = energy;
        this.maxReceive = maxReceive;
        this.facing = facing;
    }

    @Override
    public void setMaxEnergy(int value) {
        this.maxEnergy = value;
    }

    @Override
    public void setEnergy(int value) {
        this.energy = value;
    }

    @Override
    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    @Override
    public int getEnergy() {
        return this.energy;
    }

    @Override
    public boolean canReceive(EnumFacing side) {

        if(this.energy != this.maxEnergy)
            return(side.equals(this.facing));

        return false;

    }

    @Override
    public int receiveEnergy(int maxReceive, EnumFacing side, boolean simulate) {

        if(!canReceive(side))
            return 0;

        int energyReceived = Math.min(this.maxEnergy - this.energy, Math.min(this.maxReceive, maxReceive));

        if (!simulate)
            energy += energyReceived;

        return energyReceived;

    }

    @Override
    public void setMaxReceive(int value) {
        this.maxReceive = value;
    }

    @Override
    public int getMaxReceive() {
        return this.maxReceive;
    }
}
