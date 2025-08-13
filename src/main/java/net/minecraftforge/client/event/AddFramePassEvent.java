/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.client.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.FramePassManager;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Fired after all vanilla frame passes are added into the pass list.
 *
 * <p>This event is fired on the {@linkplain net.minecraftforge.common.MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide#CLIENT logical client}.
 */
@NullMarked
public record AddFramePassEvent() implements RecordEvent {
    public static final EventBus<AddFramePassEvent> BUS = EventBus.create(AddFramePassEvent.class);
    /**
     * Adds a frame pass to pass list.
     * Create a new {@linkplain FramePassManager.PassDefinition} to handle render targets and render code.
     *
     * @param rl Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @param definition see usages of {@linkplain com.mojang.blaze3d.framegraph.FramePass} in {@linkplain net.minecraft.client.renderer.LevelRenderer}
     * @throws IllegalArgumentException If the name is a duplicate.
     */
    public void addPass(ResourceLocation rl, FramePassManager.PassDefinition definition) {
        ForgeHooksClient.addFramePass(rl, definition);
    }
}