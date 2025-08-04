/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.SelfDestructing;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.List;

/**
 * Used for registering custom picture in picture renderers.
 * <p>
 * Fired at the beginning of the loading screen when starting minecraft.
 * Cannot be used to replace vanilla picture in picture renderers.
 * This event is fired only on the {@linkplain LogicalSide#CLIENT logical client}
 */
public final class RegisterPictureInPictureRendererEvent extends MutableEvent implements SelfDestructing {
    public static final EventBus<RegisterPictureInPictureRendererEvent> BUS = EventBus.create(RegisterPictureInPictureRendererEvent.class);

    private final List<PictureInPictureRenderer<?>> renderers;
    private final MultiBufferSource.BufferSource bufferSource;
    private final ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder;

    @ApiStatus.Internal
    public RegisterPictureInPictureRendererEvent(List<PictureInPictureRenderer<?>> renderers, MultiBufferSource.BufferSource bufferSource, ImmutableMap.Builder<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> builder) {
        this.renderers = renderers;
        this.bufferSource = bufferSource;
        this.builder = builder;
    }

    public MultiBufferSource.BufferSource getBufferSource() {
        return bufferSource;
    }

    public void register(PictureInPictureRenderer<?> renderer) {
        var seen = HashSet.newHashSet(renderers.size());
        for (var r : renderers)
            seen.add(r.getRenderStateClass());
        var key = renderer.getRenderStateClass();
        if (seen.add(key))
            this.builder.put(key, renderer);
    }
}
