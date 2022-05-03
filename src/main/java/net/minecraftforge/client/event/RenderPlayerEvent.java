/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final PlayerRenderer renderer;
    private final float partialRenderTick;
    private final MatrixStack stack;
    private final IRenderTypeBuffer buffers;
    private final int light;

    public RenderPlayerEvent(PlayerEntity player, PlayerRenderer renderer, float partialRenderTick, MatrixStack stack, IRenderTypeBuffer buffers, int light)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.stack = stack;
        this.buffers = buffers;
        this.light = light;
    }

    public PlayerRenderer getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public MatrixStack getMatrixStack() { return stack; }
    public IRenderTypeBuffer getBuffers() { return buffers; }
    public int getLight() { return light; }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(PlayerEntity player, PlayerRenderer renderer, float tick, MatrixStack stack, IRenderTypeBuffer buffers, int light) {
            super(player, renderer, tick, stack, buffers, light);
        }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(PlayerEntity player, PlayerRenderer renderer, float tick, MatrixStack stack, IRenderTypeBuffer buffers, int light) {
            super(player, renderer, tick, stack, buffers, light);
        }
    }

}
