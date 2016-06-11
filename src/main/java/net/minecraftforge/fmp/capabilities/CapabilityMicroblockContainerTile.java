package net.minecraftforge.fmp.capabilities;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fmp.microblock.IMicroblockContainerTile;

public class CapabilityMicroblockContainerTile
{
    @CapabilityInject(IMicroblockContainerTile.class)
    public static Capability<IMicroblockContainerTile> MICROBLOCK_CONTAINER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IMicroblockContainerTile.class, new Capability.IStorage<IMicroblockContainerTile>()
        {
            @Override
            public NBTBase writeNBT(Capability<IMicroblockContainerTile> capability, IMicroblockContainerTile instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IMicroblockContainerTile> capability, IMicroblockContainerTile instance, EnumFacing side,
                    NBTBase base)
            {
            }
        }, new Callable<IMicroblockContainerTile>()
        {
            @Override
            public IMicroblockContainerTile call() throws Exception
            {
                return null;
            }
        });
    }

}
