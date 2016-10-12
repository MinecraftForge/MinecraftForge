package net.minecraftforge.fmp.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilitySlottedCapProvider
{
    @CapabilityInject(ISlottedCapabilityProvider.class)
    public static Capability<ISlottedCapabilityProvider> SLOTTED_CAP_PROVIDER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ISlottedCapabilityProvider.class, new Capability.IStorage<ISlottedCapabilityProvider>()
        {
            @Override
            public NBTBase writeNBT(Capability<ISlottedCapabilityProvider> capability, ISlottedCapabilityProvider instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<ISlottedCapabilityProvider> capability, ISlottedCapabilityProvider instance, EnumFacing side,
                    NBTBase base)
            {
            }
        }, new Callable<ISlottedCapabilityProvider>()
        {
            @Override
            public ISlottedCapabilityProvider call() throws Exception
            {
                return null;
            }
        });
    }

}
