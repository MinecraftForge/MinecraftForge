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
    private final ItemStack itemStack;
    private final ItemFrame itemFrameEntity;
    private final ItemFrameRenderer renderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    public RenderItemInFrameEvent(ItemFrame itemFrame, ItemFrameRenderer renderItemFrame, PoseStack poseStack,
                                  MultiBufferSource multiBufferSource, int packedLight)
    {
        itemStack = itemFrame.getItem();
        itemFrameEntity = itemFrame;
        renderer = renderItemFrame;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    @Nonnull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public ItemFrame getItemFrameEntity()
    {
        return itemFrameEntity;
    }

    public ItemFrameRenderer getRenderer()
    {
        return renderer;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }
}
