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

package net.minecraftforge.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraftforge.eventbus.api.Event;

public class RenderItemEvent extends Event {
    private LivingEntity entity;
    private HandSide handSide;
    private ItemStack heldItem;
    private ItemCameraTransforms.TransformType transformType;
    private MatrixStack matrixStack;
    private IRenderTypeBuffer renderTypeBuffer;
    private int light;

    public RenderItemEvent(LivingEntity entity, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, HandSide handSide, int light) {
        this.entity = entity;
        this.handSide = handSide;
        this.heldItem = heldItem;
        this.transformType = transformType;
        this.matrixStack = matrixStack;
        this.renderTypeBuffer = renderTypeBuffer;
        this.light = light;
    }

    public ItemStack getItem() {
        return this.heldItem;
    }

    public ItemCameraTransforms.TransformType getTransformType() {
        return this.transformType;
    }

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public IRenderTypeBuffer getRenderTypeBuffer() {
        return this.renderTypeBuffer;
    }

    public int getLight() {
        return this.light;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public HandSide getHandSide() {
        return this.handSide;
    }

    public static class Post extends RenderItemEvent {
        public Post(LivingEntity entity, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, HandSide handSide, int p_2291357) {
            super(entity, heldItem, transformType, matrixStack, renderTypeBuffer, handSide, p_2291357);
        }
    }

    public static class Pre extends RenderItemEvent {
        public Pre(LivingEntity entity, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, HandSide handSide, int p_2291357) {
            super(entity, heldItem, transformType, matrixStack, renderTypeBuffer, handSide, p_2291357);
        }
    }
}
