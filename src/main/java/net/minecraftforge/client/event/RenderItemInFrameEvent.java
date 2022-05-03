/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
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
    private final ItemFrameEntity entityItemFrame;
    private final ItemFrameRenderer renderer;
    private final MatrixStack matrix;
    private final IRenderTypeBuffer buffers;
    private final int light;

    public RenderItemInFrameEvent(ItemFrameEntity itemFrame, ItemFrameRenderer renderItemFrame, MatrixStack matrix,
                                  IRenderTypeBuffer buffers, int light)
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

    public ItemFrameEntity getEntityItemFrame()
    {
        return entityItemFrame;
    }

    public ItemFrameRenderer getRenderer()
    {
        return renderer;
    }

    public MatrixStack getMatrix() {
        return matrix;
    }

    public IRenderTypeBuffer getBuffers() {
        return buffers;
    }

    public int getLight() {
        return light;
    }
}
