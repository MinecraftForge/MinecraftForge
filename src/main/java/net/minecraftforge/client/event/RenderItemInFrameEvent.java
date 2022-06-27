/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before an item stack is rendered in an item frame.
 * This can be used to prevent normal rendering or add custom rendering.
 *
 * <p>This event is {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * If the event is cancelled, then the item stack will not be rendered</p>
 *
 * <p>This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * @see ItemFrameRenderer
 */
@Cancelable
public class RenderItemInFrameEvent extends Event
{
    private final ItemStack itemStack;
    private final ItemFrame itemFrameEntity;
    private final ItemFrameRenderer<?> renderer;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    @ApiStatus.Internal
    public RenderItemInFrameEvent(ItemFrame itemFrame, ItemFrameRenderer<?> renderItemFrame, PoseStack poseStack,
                                  MultiBufferSource multiBufferSource, int packedLight)
    {
        itemStack = itemFrame.getItem();
        itemFrameEntity = itemFrame;
        renderer = renderItemFrame;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    /**
     * {@return the item stack being rendered}
     */
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * {@return the item frame entity}
     */
    public ItemFrame getItemFrameEntity()
    {
        return itemFrameEntity;
    }

    /**
     * {@return the renderer for the item frame entity}
     */
    public ItemFrameRenderer<?> getRenderer()
    {
        return renderer;
    }

    /**
     * {@return the pose stack used for rendering}
     */
    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    /**
     * {@return the source of rendering buffers}
     */
    public MultiBufferSource getMultiBufferSource()
    {
        return multiBufferSource;
    }

    /**
     * {@return the amount of packed (sky and block) light for rendering}
     *
     * @see LightTexture
     */
    public int getPackedLight()
    {
        return packedLight;
    }
}
