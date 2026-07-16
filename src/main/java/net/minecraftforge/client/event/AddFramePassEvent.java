/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraft.resources.Identifier;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.FramePassManager;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.RecordEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Fired during the construction of {@linkplain net.minecraft.client.renderer.LevelRenderer}.
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
     * See javadocs for details on what creating a PassDefinition entails.
     *
     * @param rl Identifier for frame pass name. Use RLs to avoid duplicate names.
     * @param definition see usages of {@linkplain com.mojang.blaze3d.framegraph.FramePass} in {@linkplain net.minecraft.client.renderer.LevelRenderer}
     * @throws IllegalArgumentException If the name is a duplicate.
     */
    public void addPass(Identifier rl, FramePassManager.PassDefinition definition) {
        ForgeHooksClient.addFramePass(rl, definition);
    }
}