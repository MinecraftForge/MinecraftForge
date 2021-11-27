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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * RenderItemEvent is fired if rendering a Item.
 * <br>
 * This event is {@link Cancelable}. <br>
 * If the event is canceled, the item render not anymore.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/

@Cancelable
public class RenderItemEvent extends Event {
    private LivingEntity entity;
    private HandSide handSide;
    private ItemStack heldItem;
    private final ItemCameraTransforms.TransformType transformType;
    private final MatrixStack matrixStack;
    private final IRenderTypeBuffer renderTypeBuffer;
    private final int light;

    public RenderItemEvent(LivingEntity entity, HandSide handSide, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
    {
        this.entity = entity;
        this.handSide = handSide;
        this.heldItem = heldItem;
        this.transformType = transformType;
        this.matrixStack = matrixStack;
        this.renderTypeBuffer = renderTypeBuffer;
        this.light = light;
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public HandSide getHandSide()
    {
        return this.handSide;
    }

    public ItemStack getItem()
    {
        return this.heldItem;
    }

    public ItemCameraTransforms.TransformType getTransformType()
    {
        return this.transformType;
    }

    public MatrixStack getMatrixStack()
    {
        return this.matrixStack;
    }

    public IRenderTypeBuffer getRenderTypeBuffer()
    {
        return this.renderTypeBuffer;
    }

    public int getLight()
    {
        return this.light;
    }

    @Cancelable
    public static class Pre extends RenderItemEvent
    {
        public Pre(LivingEntity entity, HandSide handSide, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
        {
            super(entity, handSide, heldItem, transformType, matrixStack, renderTypeBuffer, light);
        }
    }

    public static class Post extends RenderItemEvent
    {
        public Post(LivingEntity entity, HandSide handSide, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light)
        {
            super(entity, handSide, heldItem, transformType, matrixStack, renderTypeBuffer, light);
        }
    }
}
