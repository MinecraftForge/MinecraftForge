/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Consumer;

/**
 * Defines extension methods on {@link RenderType} for use in Forge.
 */
public interface IForgeRenderType
{

    /**
     * The current render type.
     * @return The current render type (basically this)
     * @implNote This is not allowed to be default implemented and double cast. It must be implemented by the target, for performance reasons (The unchecked cast is expensive)!
     */
    RenderType me();

    /**
     * Renders the given CPU buffer with this render type without setting up sorting!
     *
     * @param renderedBuffer The CPU buffer representation.
     * @param afterSetupAction The callback invoked after the render state has been set up.
     * @param afterRenderAction The callback invoked after the render state has been cleared.
     */
    default void doRender(BufferBuilder.RenderedBuffer renderedBuffer, Consumer<ShaderInstance> afterSetupAction, Consumer<ShaderInstance> afterRenderAction) {
        me().setupRenderState();
        afterSetupAction.accept(RenderSystem.getShader());
        BufferUploader.drawWithShader(renderedBuffer);
        afterRenderAction.accept(RenderSystem.getShader());
        me().clearRenderState();
    }
}
