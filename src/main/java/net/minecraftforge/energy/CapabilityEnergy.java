package net.minecraftforge.energy;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
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
            public NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side)
            {
                return new NBTTagInt(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, EnumFacing side, NBTBase nbt)
            {
                if (!(instance instanceof EnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((EnergyStorage)instance).energy = ((NBTTagInt)nbt).getInt();
            }
        },
        new Callable<IEnergyStorage>()
        {
            @Override
            public IEnergyStorage call() throws Exception
            {
                return new EnergyStorage(1000);
            }
        });
    }
}
