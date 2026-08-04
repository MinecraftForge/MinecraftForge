/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    private final PlayerRenderer renderer;
    private final float partialRenderTick;
    private final double x;
    private final double y;
    private final double z;

    public RenderPlayerEvent(PlayerEntity player, PlayerRenderer renderer, float partialRenderTick, double x, double y, double z)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PlayerRenderer getRenderer() { return renderer; }
    public float getPartialRenderTick() { return partialRenderTick; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    @net.minecraftforge.eventbus.api.Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(PlayerEntity player, PlayerRenderer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(PlayerEntity player, PlayerRenderer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
    }
    
}
