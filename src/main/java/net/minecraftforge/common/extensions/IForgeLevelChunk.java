/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.world.level.chunk.LevelChunk;

public interface IForgeLevelChunk extends net.minecraftforge.common.capabilities.ICapabilityProviderImpl<LevelChunk>
{
    private LevelChunk self() {return (LevelChunk) this;}
}
