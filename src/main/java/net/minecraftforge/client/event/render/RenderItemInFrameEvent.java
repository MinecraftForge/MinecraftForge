/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.event.render;

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
    private final ItemFrameRenderer<?> renderer;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int light;

    public RenderItemInFrameEvent(ItemFrame itemFrame, ItemFrameRenderer<?> renderItemFrame, PoseStack poseStack,
                                  MultiBufferSource bufferSource, int light)
    {
        this.item = itemFrame.getItem();
        this.entityItemFrame = itemFrame;
        this.renderer = renderItemFrame;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.light = light;
    }

    @Nonnull
    public ItemStack getItemStack()
    {
        return item;
    }

    public ItemFrame getEntityItemFrame()
    {
        return entityItemFrame;
    }

    public ItemFrameRenderer<?> getRenderer()
    {
        return renderer;
    }

    public PoseStack getPoseStack()
    {
        return poseStack;
    }

    public MultiBufferSource getBufferSource()
    {
        return bufferSource;
    }

    public int getLight()
    {
        return light;
    }
}
