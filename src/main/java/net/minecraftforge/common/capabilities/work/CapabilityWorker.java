package net.minecraftforge.common.capabilities.work;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * Capability for {@link IWorker}.
 * @author rubensworks
 */
public class CapabilityWorker
{
    @CapabilityInject(IWorker.class)
    public static Capability<IWorker> WORKER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IWorker.class, new Capability.IStorage<IWorker>()
        {
            @Override
            public NBTBase writeNBT(Capability<IWorker> capability, IWorker instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IWorker> capability, IWorker instance, EnumFacing side, NBTBase base)
            {
            }
        }, new Callable<DefaultWorker>()
        {
            @Override
            public DefaultWorker call() throws Exception
            {
                return new DefaultWorker();
            }
        });
    }

}
