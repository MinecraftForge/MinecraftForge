/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

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
public class RenderItemInFrameEvent extends net.minecraftforge.eventbus.api.Event
{
    private final ItemStack item;
    private final ItemFrameEntity entityItemFrame;
    private final ItemFrameRenderer renderer;

    public RenderItemInFrameEvent(ItemFrameEntity itemFrame, ItemFrameRenderer renderItemFrame)
    {
        item = itemFrame.getDisplayedItem();
        entityItemFrame = itemFrame;
        renderer = renderItemFrame;
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
}
