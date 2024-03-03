/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * This event is called when an item is rendered in an item frame.
 *
 * You can set canceled to do no further vanilla processing.
 */
@Cancelable
public class RenderItemInFrameEvent extends Event
{
    private final ItemStack item;
    private final ItemFrame entityItemFrame;
    private final ItemFrameRenderer renderer;
    private final PoseStack matrix;
    private final MultiBufferSource buffers;
    private final int light;

    public RenderItemInFrameEvent(ItemFrame itemFrame, ItemFrameRenderer renderItemFrame, PoseStack matrix,
                                  MultiBufferSource buffers, int light)
    {
        item = itemFrame.getItem();
        entityItemFrame = itemFrame;
        renderer = renderItemFrame;
        this.matrix = matrix;
        this.buffers = buffers;
        this.light = light;
    }

    @Nonnull
    public ItemStack getItem()
    {
        return item;
    }

    public ItemFrame getEntityItemFrame()
    {
        return entityItemFrame;
    }

    public ItemFrameRenderer getRenderer()
    {
        return renderer;
    }

    public PoseStack getMatrix() {
        return matrix;
    }

    public MultiBufferSource getBuffers() {
        return buffers;
    }

    public int getLight() {
        return light;
    }
}
