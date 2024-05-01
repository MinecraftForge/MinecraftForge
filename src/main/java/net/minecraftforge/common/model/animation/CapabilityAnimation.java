/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.model.animation;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityAnimation
{
    public static Capability<IAnimationStateMachine> ANIMATION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.register(IAnimationStateMachine.class);
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
