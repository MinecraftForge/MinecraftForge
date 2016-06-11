package net.minecraftforge.fmp.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fmp.multipart.IMultipartContainer;

public class CapabilityMultipartContainer
{
    @CapabilityInject(IMultipartContainer.class)
    public static Capability<IMultipartContainer> MULTIPART_CONTAINER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IMultipartContainer.class, new Capability.IStorage<IMultipartContainer>()
        {
            @Override
            public NBTBase writeNBT(Capability<IMultipartContainer> capability, IMultipartContainer instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IMultipartContainer> capability, IMultipartContainer instance, EnumFacing side, NBTBase base)
            {
            }
        }, new Callable<IMultipartContainer>()
        {
            @Override
            public IMultipartContainer call() throws Exception
            {
                return null;
            }
        });
    }

}
