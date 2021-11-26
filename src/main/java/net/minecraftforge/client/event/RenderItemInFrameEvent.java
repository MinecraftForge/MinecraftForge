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
