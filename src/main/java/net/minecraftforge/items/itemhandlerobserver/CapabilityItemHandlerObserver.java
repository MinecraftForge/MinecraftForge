package net.minecraftforge.items.itemhandlerobserver;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityItemHandlerObserver
{
    @CapabilityInject(IItemHandlerObservable.class)
    public static Capability<IItemHandlerObservable> ITEMHANDLER_OBSERVER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IItemHandlerObservable.class, new Capability.IStorage<IItemHandlerObservable>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IItemHandlerObservable> capability, IItemHandlerObservable instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemHandlerObservable> capability, IItemHandlerObservable instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, DefaultObservable::new);
    }
}
