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
    private int p_2291357;

    public RenderItemEvent(LivingEntity entity, ItemStack heldItem, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, HandSide handSide, int p_2291357) {
        this.entity = entity;
        this.handSide = handSide;
        this.heldItem = heldItem;
        this.transformType = transformType;
        this.matrixStack = matrixStack;
        this.renderTypeBuffer = renderTypeBuffer;
        this.p_2291357 = p_2291357;
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

    public int getP_2291357() {
        return this.p_2291357;
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
