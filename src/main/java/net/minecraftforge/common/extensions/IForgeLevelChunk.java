/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilityEventProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public interface IForgeLevelChunk extends net.minecraftforge.common.capabilities.ICapabilityProviderImpl<LevelChunk>, ICapabilityEventProvider
{
    private LevelChunk self() {return (LevelChunk) this;}

    @Override
    @SuppressWarnings("all")
    default  <T> AttachCapabilitiesEvent<T> createAttachCapabilitiesEvent(T obj) {
        return new AttachCapabilitiesEvent.AttachLevelChunkEvent<>((T) self());
    }
}
