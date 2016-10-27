package net.minecraftforge.client.event;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class RenderItemLayerEvent extends Event
{
    private ItemStack stack;
    private int index;
    private TransformType transformType;
    
    public RenderItemLayerEvent(ItemStack stack, int index, TransformType transformType)
    {
        this.stack = stack;
        this.index = index;
        this.transformType = transformType;
    }
    
    public ItemStack getStack() { return stack; }
    public int getRenderLayer() { return index; }
    public TransformType getTransformType() { return transformType; }
    
    public static class Pre extends RenderItemLayerEvent
    {
        public Pre(ItemStack stack, int index, TransformType transformType) { super(stack, index, transformType); }
    }
    
    public static class Post extends RenderItemLayerEvent
    {
        public Post(ItemStack stack, int index, TransformType transformType) { super(stack, index, transformType); }
    }
}
