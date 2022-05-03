/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityAnimation
{
    @CapabilityInject(IAnimationStateMachine.class)
    public static Capability<IAnimationStateMachine> ANIMATION_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IAnimationStateMachine.class, new Capability.IStorage<IAnimationStateMachine>()
        {
            @Override
            public INBT writeNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, Direction side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, Direction side, INBT nbt) {}
        }, AnimationStateMachine::getMissing);
    }

    public static class DefaultItemAnimationCapabilityProvider implements ICapabilityProvider
    {
        @Nonnull
        private final LazyOptional<IAnimationStateMachine> asm;

        public DefaultItemAnimationCapabilityProvider(@Nonnull LazyOptional<IAnimationStateMachine> asm)
        {
            this.asm = asm;
        }

        @Override
        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
        {
            return ANIMATION_CAPABILITY.orEmpty(capability, asm);
        }
    }
}
