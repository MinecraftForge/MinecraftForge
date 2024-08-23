/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;

/**
 * Supplied as a param to your mod's constructor to get access
 * to the mod EventBus and various mod-specific objects.
 */
public class FMLConstructModEvent extends ParallelDispatchEvent {
    public FMLConstructModEvent(final ModContainer container, final ModLoadingStage stage) {
        super(container, stage);
    }
}
