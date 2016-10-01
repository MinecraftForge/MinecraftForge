package net.minecraftforge.energy;

import net.minecraft.util.EnumFacing;

/**
* Example implementation of an energy provider such as a generator
 */
public class EnergyProvider implements IEnergyProvider {

    protected int maxEnergy;
    protected int energy;
    protected int maxExtract;
    protected EnumFacing facing;

    public EnergyProvider(int maxEnergy, int energy, int maxExtract, EnumFacing facing){
        this.maxEnergy = maxEnergy;
        this.energy = energy;
        this.maxExtract = maxExtract;
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
    public boolean canExtract(EnumFacing side) {

        if(this.energy > 0)
            return side.equals(this.facing);

        return false;

    }

    @Override
    public int extractEnergy(int maxExtract, EnumFacing side, boolean simulate) {

        if (!canExtract(side))
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));

        if (!simulate)
            energy -= energyExtracted;

        return energyExtracted;
    }

    @Override
    public void setMaxExtract(int value) {
        this.maxExtract = value;
    }

    @Override
    public int getMaxExtract() {
        return this.maxExtract;
    }
}
