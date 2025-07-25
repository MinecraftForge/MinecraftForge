/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Fired after all vanilla frame passes are added into the pass list.
 *
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable}, and does not
 * {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain net.minecraftforge.common.MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain net.minecraftforge.fml.LogicalSide#CLIENT logical client}.
 */
public final class AddFramePassEvent extends Event {
    private final FrameGraphBuilder builder;
    private final LevelTargetBundle bundle;

    private final Set<String> addedNames = new HashSet<>();

    @ApiStatus.Internal
    public AddFramePassEvent(FrameGraphBuilder builder, LevelTargetBundle bundle) {
        this.builder = builder;
        this.bundle = bundle;
    }

    /**
     * Adds a frame pass to the end of the pass list.
     *
     * @param rl Resource location for frame pass name. Use RLs to avoid duplicate names.
     * @return Reference to pass to add modder rendering to.
     * @throws IllegalArgumentException If the name is a duplicate.
     */
    public FramePass createPass(ResourceLocation rl) {
        String name = rl.toString();
        if (!this.addedNames.add(name)) // false if the set didn't change
            throw new IllegalArgumentException("Cannot create a frame pass with a duplicate name: " + name);

        return this.builder.addPass(name);
    }

    public LevelTargetBundle getBundle() {
        return bundle;
    }
}