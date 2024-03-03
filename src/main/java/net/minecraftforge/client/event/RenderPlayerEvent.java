/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final PlayerRenderer renderer;
    private final float partialRenderTick;
    private final PoseStack stack;
    private final MultiBufferSource buffers;
    private final int light;

    public RenderPlayerEvent(Player player, PlayerRenderer renderer, float partialRenderTick, PoseStack stack, MultiBufferSource buffers, int light)
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
    public PoseStack getMatrixStack() { return stack; }
    public MultiBufferSource getBuffers() { return buffers; }
    public int getLight() { return light; }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(Player player, PlayerRenderer renderer, float tick, PoseStack stack, MultiBufferSource buffers, int light) {
            super(player, renderer, tick, stack, buffers, light);
        }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(Player player, PlayerRenderer renderer, float tick, PoseStack stack, MultiBufferSource buffers, int light) {
            super(player, renderer, tick, stack, buffers, light);
        }
    }

}
