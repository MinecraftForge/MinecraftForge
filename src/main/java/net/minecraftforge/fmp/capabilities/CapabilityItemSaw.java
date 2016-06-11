package net.minecraftforge.fmp.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fmp.item.IItemSaw;

public class CapabilityItemSaw
{
    @CapabilityInject(IItemSaw.class)
    public static Capability<IItemSaw> SLOTTED_CAP_PROVIDER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IItemSaw.class, new Capability.IStorage<IItemSaw>()
        {
            @Override
            public NBTBase writeNBT(Capability<IItemSaw> capability, IItemSaw instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemSaw> capability, IItemSaw instance, EnumFacing side, NBTBase base)
            {
            }
        }, new Callable<IItemSaw>()
        {
            @Override
            public IItemSaw call() throws Exception
            {
                return new IItemSaw.Implementation(0);
            }
        });
    }

}
