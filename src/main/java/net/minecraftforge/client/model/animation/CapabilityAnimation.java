package net.minecraftforge.client.model.animation;

import java.util.concurrent.Callable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;

public class CapabilityAnimation
{
    @CapabilityInject(IAnimationStateMachine.class)
    public static Capability<IAnimationStateMachine> ANIMATION_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IAnimationStateMachine.class, new Capability.IStorage<IAnimationStateMachine>()
        {
            public NBTBase writeNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, EnumFacing side)
            {
                return null;
            }

            public void readNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, EnumFacing side, NBTBase nbt) {}
        }, new Callable<IAnimationStateMachine>()
        {
            public IAnimationStateMachine call() throws Exception
            {
                return Animation.INSTANCE.missing;
            }
        });
    }

    public static class DefaultItemAnimationCapabilityProvider implements ICapabilityProvider
    {
        private final IAnimationStateMachine asm;

        public DefaultItemAnimationCapabilityProvider(IAnimationStateMachine asm)
        {
            this.asm = asm;
        }

        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == ANIMATION_CAPABILITY;
        }

        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if(capability == ANIMATION_CAPABILITY)
            {
                return (T)asm;
            }
            return null;
        }
    }
}
